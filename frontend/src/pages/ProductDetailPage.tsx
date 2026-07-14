import { useQuery } from '@tanstack/react-query'
import { useState } from 'react'
import { Link, useNavigate, useParams } from 'react-router-dom'
import { fetchProduct, productKeys } from '../api/products'
import { Alert } from '../components/Alert'
import { LoadingSpinner } from '../components/LoadingSpinner'
import { useCart } from '../hooks/useCart'
import { formatCurrency } from '../utils/format'

/**
 * Product detail page with quantity selector and add-to-cart action.
 */
export const ProductDetailPage = () => {
  const { id } = useParams<{ id: string }>()
  const productId = Number(id)
  const navigate = useNavigate()
  const { addItem } = useCart()
  const [quantity, setQuantity] = useState(1)
  const [addedMessage, setAddedMessage] = useState<string | null>(null)

  const { data: product, isLoading, isError, error } = useQuery({
    queryKey: productKeys.detail(productId),
    queryFn: () => fetchProduct(productId),
    enabled: Number.isFinite(productId) && productId > 0,
  })

  if (!Number.isFinite(productId) || productId <= 0) {
    return (
      <Alert variant="error" title="Invalid product">
        The product id in the URL is not valid.
      </Alert>
    )
  }

  const handleQuantityChange = (value: number) => {
    if (!product) {
      return
    }

    const clamped = Math.max(1, Math.min(value, product.stockQuantity))
    setQuantity(clamped)
  }

  const handleAddToCart = () => {
    if (!product || product.stockQuantity < 1) {
      return
    }

    addItem(product, quantity)
    setAddedMessage(`${quantity} × ${product.name} added to cart.`)
  }

  const handleGoToCart = () => {
    navigate('/cart')
  }

  return (
    <section className="space-y-6">
      <Link
        to="/products"
        className="inline-flex text-sm font-medium text-slate-600 hover:text-slate-900"
      >
        ← Back to products
      </Link>

      {isLoading && <LoadingSpinner label="Loading product…" />}

      {isError && (
        <Alert variant="error" title="Failed to load product">
          {error instanceof Error ? error.message : 'An unexpected error occurred.'}
        </Alert>
      )}

      {product && (
        <div className="rounded-lg border border-slate-200 bg-white p-6 shadow-sm">
          <div className="space-y-2">
            <p className="text-xs font-medium uppercase tracking-wide text-slate-500">
              {product.sku}
            </p>
            <h1 className="text-3xl font-bold tracking-tight">{product.name}</h1>
            <p className="text-2xl font-semibold text-slate-900">
              {formatCurrency(product.price)}
            </p>
            <p
              className={`text-sm font-medium ${product.stockQuantity < 1 ? 'text-red-600' : 'text-emerald-700'}`}
            >
              {product.stockQuantity < 1
                ? 'Out of stock'
                : `${product.stockQuantity} available`}
            </p>
          </div>

          {product.stockQuantity > 0 && (
            <div className="mt-6 flex flex-col gap-4 sm:flex-row sm:items-end">
              <div>
                <label htmlFor="quantity" className="block text-sm font-medium text-slate-700">
                  Quantity
                </label>
                <input
                  id="quantity"
                  type="number"
                  min={1}
                  max={product.stockQuantity}
                  value={quantity}
                  onChange={(event) => handleQuantityChange(Number(event.target.value))}
                  className="mt-1 w-24 rounded-md border border-slate-300 px-3 py-2 text-sm shadow-sm focus:border-slate-500 focus:outline-none focus:ring-2 focus:ring-slate-200"
                  aria-label="Quantity to add to cart"
                />
              </div>
              <button
                type="button"
                onClick={handleAddToCart}
                className="rounded-md bg-slate-900 px-4 py-2 text-sm font-medium text-white transition-colors hover:bg-slate-800 focus:outline-none focus-visible:ring-2 focus-visible:ring-slate-400"
                aria-label={`Add ${quantity} ${product.name} to cart`}
              >
                Add to cart
              </button>
            </div>
          )}

          {addedMessage && (
            <div className="mt-4 space-y-3">
              <Alert variant="success">{addedMessage}</Alert>
              <button
                type="button"
                onClick={handleGoToCart}
                className="text-sm font-medium text-slate-700 underline hover:text-slate-900"
              >
                Go to cart
              </button>
            </div>
          )}
        </div>
      )}
    </section>
  )
}
