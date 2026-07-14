import { useCallback, useMemo, useState, type ReactNode } from 'react'
import type { Product } from '../types'
import { CartContext, type CartContextValue } from './cart-context'
export const CartProvider = ({ children }: { children: ReactNode }) => {
  const [items, setItems] = useState<CartContextValue['items']>([])

  const addItem = useCallback((product: Product, quantity: number) => {
    if (quantity < 1) {
      return
    }

    setItems((current) => {
      const existing = current.find((item) => item.productId === product.id)

      if (!existing) {
        return [
          ...current,
          {
            productId: product.id,
            name: product.name,
            sku: product.sku,
            price: product.price,
            quantity: Math.min(quantity, product.stockQuantity),
            stockQuantity: product.stockQuantity,
          },
        ]
      }

      const nextQuantity = Math.min(existing.quantity + quantity, product.stockQuantity)

      return current.map((item) =>
        item.productId === product.id
          ? { ...item, quantity: nextQuantity, stockQuantity: product.stockQuantity }
          : item,
      )
    })
  }, [])

  const updateQuantity = useCallback((productId: number, quantity: number) => {
    if (quantity < 1) {
      setItems((current) => current.filter((item) => item.productId !== productId))
      return
    }

    setItems((current) =>
      current.map((item) =>
        item.productId === productId
          ? { ...item, quantity: Math.min(quantity, item.stockQuantity) }
          : item,
      ),
    )
  }, [])

  const removeItem = useCallback((productId: number) => {
    setItems((current) => current.filter((item) => item.productId !== productId))
  }, [])

  const clearCart = useCallback(() => {
    setItems([])
  }, [])

  const itemCount = useMemo(
    () => items.reduce((total, item) => total + item.quantity, 0),
    [items],
  )

  const subtotal = useMemo(
    () => items.reduce((total, item) => total + item.price * item.quantity, 0),
    [items],
  )

  const value = useMemo(
    () => ({
      items,
      itemCount,
      subtotal,
      addItem,
      updateQuantity,
      removeItem,
      clearCart,
    }),
    [items, itemCount, subtotal, addItem, updateQuantity, removeItem, clearCart],
  )

  return <CartContext.Provider value={value}>{children}</CartContext.Provider>
}
