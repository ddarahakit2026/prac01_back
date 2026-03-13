<script setup>
import {ref, reactive, onMounted} from 'vue'
import axios from "axios";
import PortOne from "@portone/browser-sdk/v2"

const boardList = ref([])
const totalPrice = ref(0)
const selected = ref([])
const isPaymentProcessing = ref(false)

// 결제 상태
const paymentStatus = ref({
  status: "",
  message: ""
});

const getBoardList = async () => {
  const res = await axios.get('http://localhost:8080/board/list')
  if (res.data.success && res.data.result) {
    boardList.value = res.data.result.content || []
  }
}

const toggleSelected = (board) => {
  const index = selected.value.indexOf(board)
  if (index > -1) {
    selected.value.splice(index, 1)
  } else {
    selected.value.push(board)
  }

  totalPrice.value = selected.value.reduce((acc, cur) => acc + cur.price, 0)
}

const onPayment = async () => {
  if (selected.length === 0) return
  if (isPaymentProcessing.value) return

  isPaymentProcessing.value = true
  paymentStatus.value = {status: "", message: ""}

  let ordersIdx = null

  const firstItem = selected.value[0]
  const boardIdxList = selected.value.map(board => board.idx)
  const orderName = selected.value.length === 1
      ? firstItem.title
      : `${firstItem.title} 외 ${selected.value.length - 1}건`

  // 주문 생성
  const createResponse = await axios.post('http://localhost:8080/orders/create', {
        paymentPrice: totalPrice.value,
        boardIdxList
      }, {
        withCredentials: true
      }
  )

  ordersIdx = createResponse.data.result.ordersIdx


  const paymentId = Math.floor(Math.random() * 101);
  // 결제창 띄우기
  const payment = await PortOne.requestPayment({
    storeId: "store-a98efd4b-3978-4db3-ac72-59949fba4f1e",
    channelKey: "channel-key-996b09cf-d516-4091-8e08-5882ef3479f8",
    paymentId: "imp_923865ifdg7ig" + paymentId,
    orderName: orderName,
    totalAmount: totalPrice.value,
    currency: 'KRW',
    payMethod: "CARD",
    customData: {ordersIdx, boardIdxList}
  }).then((res) => {
    return res;
  }).catch((error) => {
    paymentStatus.value = {status: "FAILED", message: '결제 시도가 실패하였습니다. 잠시 후 다시 시도해주세요.'}
  });

  // 3. 결제 검증
  const verifyResponse = await axios.post('http://localhost:8080/orders/verify', {paymentId: payment.paymentId})
}

onMounted(async () => {
  await getBoardList()
})
</script>

<template>

  <div v-for="board in boardList" :key="board.idx">
    <div>
      <input type="checkbox" :checked="selected.includes(board.idx)"
             @change="toggleSelected(board)">
      <span>{{ board.idx }}</span> <span>{{ board.title }}</span> <span>{{ board.price }}</span>
    </div>
  </div>
  <button @click="onPayment">결제하기</button>

</template>

<style scoped>


</style>
