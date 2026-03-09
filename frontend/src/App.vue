<script setup>
import {ref} from "vue";
import {Client} from '@stomp/stompjs'

const localStream = ref(null);
const localVideo = ref(null);
const remoteVideo = ref(null);
const socket = ref(null);
let peerConnection = null;
const senderId = Math.random().toString(36).substring(2, 9);

const config = {
  iceServers: [
    {urls: 'stun:stun.l.google.com:19302'}
  ]
};

// 영상 및 오디오 스펙을 상대방에게 제안하는 메시지를 받았을 때 처리할 함수
const handleOffer = async (data) => {
  if (!peerConnection) {
    createPeerConnection()
  }
  await peerConnection.setRemoteDescription(
      new RTCSessionDescription(data.offer)
  );
  const answer = await peerConnection.createAnswer();
  await peerConnection.setLocalDescription(answer);

  socket.value.publish({
    destination: '/app/webrtc',
    body: JSON.stringify({
      type: 'answer',
      answer: answer,
      senderId: senderId
    })
  })

}

// 제안에 대한 응답을 받았을 때 처리할 함수, 영상 및 오디오 스펙을 담아서 응답
const handleAnswer = async (data) => {
  console.log(data);

  await peerConnection.setRemoteDescription(
      new RTCSessionDescription(data.answer)
  );
}

// 영상 및 오디오 스펙 합의가 끝나면 실제 데이터를 주고받을 네트워크 주소를 교환하는 함수
//  일반적으로 각 피어(클라이언트)는 공유기 내부에 있기 때문에 자신의 실제 공인 IP를 알 수 없다
//  그래서 STUN 서버를 이용해서 알아낸 자신의 실제 공인 IP, 포트 번호를 이용해서 접속 가능한 경로를 교환
const handleCandidate = async (data) => {
  console.log(data);
  await peerConnection.addIceCandidate(
      new RTCIceCandidate(data.candidate)
  );
}

const createPeerConnection = () => {
  if (peerConnection) {
    return;
  }

  peerConnection = new RTCPeerConnection(config)

  if(localStream.value) {
    localStream.value.getTracks().forEach((track) => {
      peerConnection.addTrack(track, localStream.value)
    })
  }
  peerConnection.onicecandidate = (event) => {
    if (event.candidate) {
      socket.value.publish({
        destination: '/app/webrtc',
        body: JSON.stringify({
          type: 'candidate',
          candidate: event.candidate,
          senderId: senderId
        })
      })
    }
  }

  peerConnection.ontrack = (event) => {
    if (remoteVideo.value) {
      remoteVideo.value.srcObject = event.streams[0]
    }
  }
}

const makeCall = async () => {
  createPeerConnection()
  const offer = await peerConnection.createOffer();
  peerConnection.setLocalDescription(offer);

  socket.value.publish({
    destination: '/app/webrtc',
    body: JSON.stringify({
      type: 'offer',
      offer: offer,
      senderId: senderId
    })
  })
}


const startCamera = async () => {
  localStream.value = await navigator.mediaDevices.getUserMedia({
    video: true,
    audio: true
  });
  localVideo.value.srcObject = localStream.value;

  // 웹 소켓 연결
  socket.value = new Client({
    brokerURL: 'ws://localhost:5173/ws',
  });

  socket.value.onConnect = () => {
    console.log("웹 소켓 연결 성공");
    socket.value.subscribe('/topic/webrtc', async (message) => {
      console.log(message)
      const data = JSON.parse(message.body);

      if (data.senderId === senderId) {
        return;
      }

      if (data.type == 'offer') {
        await handleOffer(data);
      } else if (data.type == 'answer') {
        await handleAnswer(data);
      } else if (data.type == 'candidate') {
        await handleCandidate(data);
      }
    })
  }

  socket.value.activate()
}
</script>
<template>
  <h1>기본 WebRTC 화상 공유 데모</h1>
  <div class="controls">
    <button @click="startCamera">카메라 시작</button>
    <button @click="makeCall">연결 요청</button>
    <button id="btnHangup" disabled>연결 종료</button>
  </div>

  <div class="videos">
    <div>
      <div>내 화면</div>
      <video ref="localVideo" autoplay playsinline muted></video>
    </div>
    <div>
      <div>상대 화면</div>
      <video ref="remoteVideo" autoplay playsinline></video>
    </div>
  </div>

  <div class="log" id="log"></div>

</template>

<style scoped>
body {
  font-family: system-ui, sans-serif;
  margin: 0;
  padding: 20px;
  background: #f4f4f5;
}

h1 {
  margin-bottom: 10px;
}

.videos {
  display: flex;
  gap: 16px;
  margin-top: 16px;
}

video {
  width: 360px;
  height: 270px;
  background: #000;
  border-radius: 8px;
  object-fit: cover;
}

.controls {
  margin-top: 10px;
  display: flex;
  gap: 8px;
}

button {
  padding: 8px 12px;
  border-radius: 6px;
  border: 1px solid #ddd;
  cursor: pointer;
  background: white;
}

button:disabled {
  opacity: 0.5;
  cursor: default;
}

.log {
  margin-top: 16px;
  padding: 8px;
  border-radius: 6px;
  background: #e5e7eb;
  font-size: 13px;
  max-height: 160px;
  overflow-y: auto;
  white-space: pre-wrap;
}
</style>
