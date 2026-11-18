import { reactive } from 'vue'

const state = reactive({ items: [] })
let seed = 0

export function toast(message, type = 'success') {
  const id = ++seed
  state.items.push({ id, message, type })
  setTimeout(() => {
    const i = state.items.findIndex((x) => x.id === id)
    if (i > -1) state.items.splice(i, 1)
  }, 2600)
}

toast.success = (m) => toast(m, 'success')
toast.error = (m) => toast(m, 'error')

export const toastState = state
