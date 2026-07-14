import { useQuery } from '@tanstack/react-query'
import { useState } from 'react'
import { fetchProducts, productKeys } from '../api/products'
import { Alert } from '../components/Alert'
import { LoadingSpinner } from '../components/LoadingSpinner'
import { Pagination } from '../components/Pagination'
import { ProductCard } from '../components/ProductCard'
import { useDebounce } from '../hooks/useDebounce'

const PAGE_SIZE = 12

/**
 * Paginated product catalog with debounced search.
 */
export const ProductsPage = () => {
  const [page, setPage] = useState(0)
  const [search, setSearch] = useState('')
  const debouncedSearch = useDebounce(search, 300)

  const { data, isLoading, isError, error } = useQuery({
    queryKey: productKeys.list({ page, size: PAGE_SIZE, search: debouncedSearch, active: true }),
    queryFn: () =>
      fetchProducts({ page, size: PAGE_SIZE, search: debouncedSearch || undefined, active: true }),
  })

  const handleSearchChange = (value: string) => {
    setSearch(value)
    setPage(0)
  }

  return (
    <section className="space-y-6">
      <div className="flex flex-col gap-4 sm:flex-row sm:items-end sm:justify-between">
        <div>
          <h1 className="text-2xl font-bold tracking-tight">Products</h1>
          <p className="mt-1 text-slate-600">Browse the catalog and add items to your cart.</p>
        </div>
        <div className="w-full sm:max-w-xs">
          <label htmlFor="product-search" className="sr-only">
            Search products
          </label>
          <input
            id="product-search"
            type="search"
            value={search}
            onChange={(event) => handleSearchChange(event.target.value)}
            placeholder="Search by name or SKU…"
            className="w-full rounded-md border border-slate-300 px-3 py-2 text-sm shadow-sm focus:border-slate-500 focus:outline-none focus:ring-2 focus:ring-slate-200"
            aria-label="Search products by name or SKU"
          />
        </div>
      </div>

      {isLoading && <LoadingSpinner label="Loading products…" />}

      {isError && (
        <Alert variant="error" title="Failed to load products">
          {error instanceof Error ? error.message : 'An unexpected error occurred.'}
        </Alert>
      )}

      {data && data.content.length === 0 && (
        <Alert variant="info">No products match your search.</Alert>
      )}

      {data && data.content.length > 0 && (
        <>
          <p className="text-sm text-slate-500">
            Showing {data.content.length} of {data.totalElements} products
          </p>
          <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
            {data.content.map((product) => (
              <ProductCard key={product.id} product={product} />
            ))}
          </div>
          <Pagination page={page} totalPages={data.totalPages} onPageChange={setPage} />
        </>
      )}
    </section>
  )
}
