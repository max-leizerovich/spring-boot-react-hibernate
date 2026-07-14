/**
 * Centered loading indicator for async page content.
 */
export const LoadingSpinner = ({ label = 'Loading…' }: { label?: string }) => {
  return (
    <div className="flex items-center justify-center py-16" role="status" aria-live="polite">
      <div className="flex items-center gap-3 text-slate-600">
        <span
          className="inline-block h-5 w-5 animate-spin rounded-full border-2 border-slate-300 border-t-slate-700"
          aria-hidden="true"
        />
        <span>{label}</span>
      </div>
    </div>
  )
}
