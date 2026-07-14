type AlertProps = {
  variant: 'error' | 'success' | 'info'
  title?: string
  children: React.ReactNode
}

const variantClasses: Record<AlertProps['variant'], string> = {
  error: 'border-red-200 bg-red-50 text-red-800',
  success: 'border-emerald-200 bg-emerald-50 text-emerald-800',
  info: 'border-slate-200 bg-white text-slate-700',
}

/**
 * Accessible alert banner for status and error messages.
 */
export const Alert = ({ variant, title, children }: AlertProps) => {
  const role = variant === 'error' ? 'alert' : 'status'

  return (
    <div
      className={`rounded-lg border px-4 py-3 text-sm ${variantClasses[variant]}`}
      role={role}
      aria-live="polite"
    >
      {title && <p className="font-semibold">{title}</p>}
      <div className={title ? 'mt-1' : undefined}>{children}</div>
    </div>
  )
}
