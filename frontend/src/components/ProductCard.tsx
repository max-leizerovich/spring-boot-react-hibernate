import { Link } from 'react-router-dom'
import type { Product } from '../types'
import { formatCurrency } from '../utils/format'

type ProductCardProps = {
  product: Product
}

/**
 * Catalog card linking to the product detail page.
 */
export const ProductCard = ({ product }: ProductCardProps) => {
  const outOfStock = product.stockQuantity < 1

  return (
    <article className="flex flex-col rounded-lg border border-slate-200 bg-white p-5 shadow-sm transition-shadow hover:shadow-md">
      <div className="flex-1 space-y-2">
        <h2 className="text-lg font-semibold text-slate-900">
          <Link
            to={`/products/${product.id}`}
            className="hover:text-slate-700 focus:outline-none focus-visible:ring-2 focus-visible:ring-slate-400"
            aria-label={`View details for ${product.name}`}
          >
            {product.name}
          </Link>
        </h2>
        <p className="text-xs font-medium uppercase tracking-wide text-slate-500">{product.sku}</p>
        <p className="text-xl font-bold text-slate-900">{formatCurrency(product.price)}</p>
      </div>
      <div className="mt-4 flex items-center justify-between">
        <span
          className={`text-sm font-medium ${outOfStock ? 'text-red-600' : 'text-emerald-700'}`}
        >
          {outOfStock ? 'Out of stock' : `${product.stockQuantity} in stock`}
        </span>
        <Link
          to={`/products/${product.id}`}
          className="rounded-md bg-slate-900 px-3 py-1.5 text-sm font-medium text-white transition-colors hover:bg-slate-800 focus:outline-none focus-visible:ring-2 focus-visible:ring-slate-400"
          tabIndex={0}
          aria-label={`View ${product.name}`}
        >
          View
        </Link>
      </div>
    </article>
  )
}
