import { useContext } from 'react'
import { CartContext, type CartContextValue } from '../context/cart-context'

/**
 * Reads the shopping cart context.
 */
export const useCart = (): CartContextValue => {
  const context = useContext(CartContext)

  if (!context) {
    throw new Error('useCart must be used within a CartProvider')
  }

  return context
}
