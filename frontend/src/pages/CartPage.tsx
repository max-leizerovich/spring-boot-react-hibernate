import { useMutation, useQueryClient } from '@tanstack/react-query'
import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { HttpError } from '../api/client'
import { createOrder, orderKeys } from '../api/orders'
import { productKeys } from '../api/products'
import { Alert } from '../components/Alert'
import { useCart } from '../hooks/useCart'
import { formatCurrency } from '../utils/format'

/**
 * Shopping cart with line-item editing and checkout.
 */
export const CartPage = () => {
  const navigate = useNavigate()
  const queryClient = useQueryClient()
  const { items, subtotal, updateQuantity, removeItem, clearCart } = useCart()
  const [checkoutError, setCheckoutError] = useState<string | null>(null)

  const checkoutMutation = useMutation({
    mutationFn: createOrder,
    retry: (failureCount, error) => {
      if (error instanceof HttpError && error.status === 409 && failureCount < 2) {
        return true
      }

      return false
    },
    onSuccess: (order) => {
      clearCart()
      void queryClient.invalidateQueries({ queryKey: productKeys.all })
      void queryClient.invalidateQueries({ queryKey: orderKeys.all })
      navigate(`/orders/${order.id}`)
    },
    onError: (error) => {
      if (error instanceof HttpError) {
        if (error.status === 409) {
          setCheckoutError(
            'Inventory changed while checking out. Product stock was refreshed — review your cart and try again.',
          )
          void queryClient.invalidateQueries({ queryKey: productKeys.all })
          return
        }

        setCheckoutError(error.message)
        return
      }

      setCheckoutError('Checkout failed. Please try again.')
    },
  })

  const handleCheckout = () => {
    if (items.length === 0) {
      return
    }

    setCheckoutError(null)
    checkoutMutation.mutate({
      items: items.map((item) => ({
        productId: item.productId,
        quantity: item.quantity,
      })),
    })
  }

  if (items.length === 0) {
    return (
      <section className="space-y-4">
        <h1 className="text-2xl font-bold tracking-tight">Cart</h1>
        <Alert variant="info">Your cart is empty.</Alert>
        <Link
          to="/products"
          className="inline-flex rounded-md bg-slate-900 px-4 py-2 text-sm font-medium text-white hover:bg-slate-800"
        >
          Browse products
        </Link>
      </section>
    )
  }

  return (
    <section className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold tracking-tight">Cart</h1>
        <p className="mt-1 text-slate-600">Review items before checkout.</p>
      </div>

      {checkoutError && (
        <Alert variant="error" title="Checkout failed">
          {checkoutError}
        </Alert>
      )}

      <div className="overflow-hidden rounded-lg border border-slate-200 bg-white shadow-sm">
        <table className="min-w-full divide-y divide-slate-200 text-sm">
          <thead className="bg-slate-50">
            <tr>
              <th scope="col" className="px-4 py-3 text-left font-semibold text-slate-700">
                Product
              </th>
              <th scope="col" className="px-4 py-3 text-left font-semibold text-slate-700">
                Price
              </th>
              <th scope="col" className="px-4 py-3 text-left font-semibold text-slate-700">
                Qty
              </th>
              <th scope="col" className="px-4 py-3 text-right font-semibold text-slate-700">
                Line total
              </th>
              <th scope="col" className="px-4 py-3">
                <span className="sr-only">Remove</span>
              </th>
            </tr>
          </thead>
          <tbody className="divide-y divide-slate-200">
            {items.map((item) => (
              <tr key={item.productId}>
                <td className="px-4 py-3">
                  <Link
                    to={`/products/${item.productId}`}
                    className="font-medium text-slate-900 hover:underline"
                  >
                    {item.name}
                  </Link>
                  <p className="text-xs text-slate-500">{item.sku}</p>
                </td>
                <td className="px-4 py-3 text-slate-700">{formatCurrency(item.price)}</td>
                <td className="px-4 py-3">
                  <input
                    type="number"
                    min={1}
                    max={item.stockQuantity}
                    value={item.quantity}
                    onChange={(event) =>
                      updateQuantity(item.productId, Number(event.target.value))
                    }
                    className="w-20 rounded-md border border-slate-300 px-2 py-1 text-sm"
                    aria-label={`Quantity for ${item.name}`}
                  />
                  <p className="mt-1 text-xs text-slate-500">Max {item.stockQuantity}</p>
                </td>
                <td className="px-4 py-3 text-right font-medium text-slate-900">
                  {formatCurrency(item.price * item.quantity)}
                </td>
                <td className="px-4 py-3 text-right">
                  <button
                    type="button"
                    onClick={() => removeItem(item.productId)}
                    className="text-sm font-medium text-red-600 hover:text-red-800"
                    aria-label={`Remove ${item.name} from cart`}
                  >
                    Remove
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      <div className="flex flex-col items-start justify-between gap-4 rounded-lg border border-slate-200 bg-white p-4 shadow-sm sm:flex-row sm:items-center">
        <div>
          <p className="text-sm text-slate-600">Subtotal</p>
          <p className="text-2xl font-bold text-slate-900">{formatCurrency(subtotal)}</p>
        </div>
        <button
          type="button"
          onClick={handleCheckout}
          disabled={checkoutMutation.isPending}
          className="rounded-md bg-emerald-600 px-5 py-2.5 text-sm font-semibold text-white transition-colors hover:bg-emerald-700 disabled:cursor-not-allowed disabled:opacity-60"
          aria-label="Place order and checkout"
        >
          {checkoutMutation.isPending ? 'Placing order…' : 'Checkout'}
        </button>
      </div>
    </section>
  )
}
