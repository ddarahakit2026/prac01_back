<script setup>
import {ref} from 'vue'
import axios from 'axios'

const message = ref('');
const socket = ref(null);

const subscribePush = async () => {
  const permission = await Notification.requestPermission()
  if(permission !== 'granted') {
    console.log("권한 없음")

    return
  }

  await navigator.serviceWorker.register('/service-worker.js')
  const VAPID_PUBLIC_KEY = 'BLHgfPga02L2u89uc4xjhbUFTy_U04rQCjGq7o24oxtqfVmAPHTxOmp6xndSHZtGQpmt7gqTFdMXco2gRNP7_p8'

  const registration = await navigator.serviceWorker.ready;
  let subscription = await registration.pushManager.getSubscription()
  if(!subscription) {
    subscription = await registration.pushManager.subscribe({
      userVisibleOnly: true,
      applicationServerKey: VAPID_PUBLIC_KEY
    })

    console.log(JSON.stringify(subscription))
    await axios.post('http://localhost:8080/push/sub', subscription)

  }

}
const connectWebSocket = () => {
  const ws = new WebSocket("ws://localhost:8080/ws")
  socket.value = ws;
}
const sendMessage = () => {
  socket.value.send(JSON.stringify(message.value))
}
</script>

<template>
  <button @click="connectWebSocket">웹 소켓 연결</button>
  <input name="message" v-model="message" />
  <button @click="sendMessage">메시지 전송</button>
  <button @click="subscribePush">알림 구독</button>
</template>

<style scoped></style>
