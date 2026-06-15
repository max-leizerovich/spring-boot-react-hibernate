import { useQuery } from '@tanstack/react-query'
import { getHealth } from '../api/health'

/**
 * Landing page with backend connectivity status.
 */
export const HomePage = () => {
  const { data, isLoading, isError, error } = useQuery({
    queryKey: ['health'],
    queryFn: getHealth,
    retry: 1,
  })

  return (
    <section className="space-y-6">
      <div>
        <h1 className="text-3xl font-bold tracking-tight">Inventory Order System</h1>
        <p className="mt-2 max-w-2xl text-slate-600">
          Practice repo for transactions, optimistic locking, caching, and checkout flows.
        </p>
      </div>

      <div
        className="rounded-lg border border-slate-200 bg-white p-6 shadow-sm"
        role="status"
        aria-live="polite"
      >
        <h2 className="text-sm font-semibold uppercase tracking-wide text-slate-500">
          API status
        </h2>
        {isLoading && <p className="mt-2 text-slate-600">Checking backend connection…</p>}
        {isError && (
          <p className="mt-2 text-red-600">
            Backend unreachable: {error instanceof Error ? error.message : 'Unknown error'}
          </p>
        )}
        {data && (
          <p className="mt-2 font-medium text-emerald-700">
            Backend is {data.status}
          </p>
        )}
      </div>
    </section>
  )
}
