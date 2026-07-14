import { createContext } from 'react'
import type { Product } from '../types'

export type CartContextValue = {
  items: import('../types').CartItem[]
  itemCount: number
  subtotal: number
  addItem: (product: Product, quantity: number) => void
  updateQuantity: (productId: number, quantity: number) => void
  removeItem: (productId: number) => void
  clearCart: () => void
}

export const CartContext = createContext<CartContextValue | null>(null)
