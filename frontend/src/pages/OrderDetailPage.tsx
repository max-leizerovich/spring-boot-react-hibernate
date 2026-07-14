import { useQuery } from '@tanstack/react-query'
import { Link, useParams } from 'react-router-dom'
import { fetchOrder, orderKeys } from '../api/orders'
import { Alert } from '../components/Alert'
import { LoadingSpinner } from '../components/LoadingSpinner'
import { StatusBadge } from '../components/StatusBadge'
import { formatCurrency, formatDateTime } from '../utils/format'

/**
 * Single order detail with line items.
 */
export const OrderDetailPage = () => {
  const { id } = useParams<{ id: string }>()
  const orderId = Number(id)

  const { data: order, isLoading, isError, error } = useQuery({
    queryKey: orderKeys.detail(orderId),
    queryFn: () => fetchOrder(orderId),
    enabled: Number.isFinite(orderId) && orderId > 0,
  })

  if (!Number.isFinite(orderId) || orderId <= 0) {
    return (
      <Alert variant="error" title="Invalid order">
        The order id in the URL is not valid.
      </Alert>
    )
  }

  return (
    <section className="space-y-6">
      <Link
        to="/orders"
        className="inline-flex text-sm font-medium text-slate-600 hover:text-slate-900"
      >
        ← Back to orders
      </Link>

      {isLoading && <LoadingSpinner label="Loading order…" />}

      {isError && (
        <Alert variant="error" title="Failed to load order">
          {error instanceof Error ? error.message : 'An unexpected error occurred.'}
        </Alert>
      )}

      {order && (
        <div className="space-y-6">
          <div className="rounded-lg border border-slate-200 bg-white p-6 shadow-sm">
            <div className="flex flex-wrap items-center gap-3">
              <h1 className="text-2xl font-bold tracking-tight">Order #{order.id}</h1>
              <StatusBadge status={order.status} />
            </div>
            <p className="mt-2 text-sm text-slate-600">
              Placed {formatDateTime(order.createdAt)}
            </p>
            <p className="mt-4 text-2xl font-bold text-slate-900">
              {formatCurrency(order.totalAmount)}
            </p>
          </div>

          {order.items && order.items.length > 0 ? (
            <div className="overflow-hidden rounded-lg border border-slate-200 bg-white shadow-sm">
              <table className="min-w-full divide-y divide-slate-200 text-sm">
                <thead className="bg-slate-50">
                  <tr>
                    <th scope="col" className="px-4 py-3 text-left font-semibold text-slate-700">
                      Product
                    </th>
                    <th scope="col" className="px-4 py-3 text-right font-semibold text-slate-700">
                      Unit price
                    </th>
                    <th scope="col" className="px-4 py-3 text-right font-semibold text-slate-700">
                      Qty
                    </th>
                    <th scope="col" className="px-4 py-3 text-right font-semibold text-slate-700">
                      Line total
                    </th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-slate-200">
                  {order.items.map((item) => (
                    <tr key={item.id}>
                      <td className="px-4 py-3">
                        <Link
                          to={`/products/${item.productId}`}
                          className="font-medium text-slate-900 hover:underline"
                        >
                          Product #{item.productId}
                        </Link>
                      </td>
                      <td className="px-4 py-3 text-right text-slate-700">
                        {formatCurrency(item.unitPrice)}
                      </td>
                      <td className="px-4 py-3 text-right text-slate-700">{item.quantity}</td>
                      <td className="px-4 py-3 text-right font-medium text-slate-900">
                        {formatCurrency(item.unitPrice * item.quantity)}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          ) : (
            <Alert variant="info">No line items found for this order.</Alert>
          )}
        </div>
      )}
    </section>
  )
}
