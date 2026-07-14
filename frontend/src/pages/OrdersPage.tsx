import { useQuery } from '@tanstack/react-query'
import { useState } from 'react'
import { Link } from 'react-router-dom'
import { fetchOrders, orderKeys } from '../api/orders'
import { Alert } from '../components/Alert'
import { LoadingSpinner } from '../components/LoadingSpinner'
import { Pagination } from '../components/Pagination'
import { StatusBadge } from '../components/StatusBadge'
import { formatCurrency, formatDateTime } from '../utils/format'

const PAGE_SIZE = 10

/**
 * Paginated order history list.
 */
export const OrdersPage = () => {
  const [page, setPage] = useState(0)

  const { data, isLoading, isError, error } = useQuery({
    queryKey: orderKeys.list({ page, size: PAGE_SIZE }),
    queryFn: () => fetchOrders({ page, size: PAGE_SIZE }),
  })

  return (
    <section className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold tracking-tight">Orders</h1>
        <p className="mt-1 text-slate-600">View your past checkouts.</p>
      </div>

      {isLoading && <LoadingSpinner label="Loading orders…" />}

      {isError && (
        <Alert variant="error" title="Failed to load orders">
          {error instanceof Error ? error.message : 'An unexpected error occurred.'}
        </Alert>
      )}

      {data && data.content.length === 0 && (
        <Alert variant="info">
          No orders yet.{' '}
          <Link to="/products" className="font-medium underline">
            Start shopping
          </Link>
          .
        </Alert>
      )}

      {data && data.content.length > 0 && (
        <>
          <div className="overflow-hidden rounded-lg border border-slate-200 bg-white shadow-sm">
            <table className="min-w-full divide-y divide-slate-200 text-sm">
              <thead className="bg-slate-50">
                <tr>
                  <th scope="col" className="px-4 py-3 text-left font-semibold text-slate-700">
                    Order
                  </th>
                  <th scope="col" className="px-4 py-3 text-left font-semibold text-slate-700">
                    Date
                  </th>
                  <th scope="col" className="px-4 py-3 text-left font-semibold text-slate-700">
                    Status
                  </th>
                  <th scope="col" className="px-4 py-3 text-right font-semibold text-slate-700">
                    Total
                  </th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-200">
                {data.content.map((order) => (
                  <tr key={order.id} className="hover:bg-slate-50">
                    <td className="px-4 py-3">
                      <Link
                        to={`/orders/${order.id}`}
                        className="font-medium text-slate-900 hover:underline"
                        aria-label={`View order ${order.id}`}
                      >
                        #{order.id}
                      </Link>
                    </td>
                    <td className="px-4 py-3 text-slate-700">
                      {formatDateTime(order.createdAt)}
                    </td>
                    <td className="px-4 py-3">
                      <StatusBadge status={order.status} />
                    </td>
                    <td className="px-4 py-3 text-right font-medium text-slate-900">
                      {formatCurrency(order.totalAmount)}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
          <Pagination page={page} totalPages={data.totalPages} onPageChange={setPage} />
        </>
      )}
    </section>
  )
}
