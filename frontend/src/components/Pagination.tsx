type PaginationProps = {
  page: number
  totalPages: number
  onPageChange: (page: number) => void
}

/**
 * Simple previous/next pagination controls.
 */
export const Pagination = ({ page, totalPages, onPageChange }: PaginationProps) => {
  if (totalPages <= 1) {
    return null
  }

  const handlePrevious = () => {
    if (page > 0) {
      onPageChange(page - 1)
    }
  }

  const handleNext = () => {
    if (page < totalPages - 1) {
      onPageChange(page + 1)
    }
  }

  return (
    <nav
      className="flex items-center justify-between border-t border-slate-200 pt-4"
      aria-label="Pagination"
    >
      <button
        type="button"
        onClick={handlePrevious}
        disabled={page === 0}
        className="rounded-md border border-slate-300 px-3 py-1.5 text-sm font-medium text-slate-700 transition-colors hover:bg-slate-50 disabled:cursor-not-allowed disabled:opacity-50"
        aria-label="Previous page"
      >
        Previous
      </button>
      <span className="text-sm text-slate-600">
        Page {page + 1} of {totalPages}
      </span>
      <button
        type="button"
        onClick={handleNext}
        disabled={page >= totalPages - 1}
        className="rounded-md border border-slate-300 px-3 py-1.5 text-sm font-medium text-slate-700 transition-colors hover:bg-slate-50 disabled:cursor-not-allowed disabled:opacity-50"
        aria-label="Next page"
      >
        Next
      </button>
    </nav>
  )
}
