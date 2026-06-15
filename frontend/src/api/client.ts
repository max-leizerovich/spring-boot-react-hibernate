import { apiErrorSchema, type ApiError } from '../types'

/** Typed HTTP error thrown by the API client. */
export class HttpError extends Error {
  readonly status: number
  readonly body: ApiError | null

  constructor(status: number, message: string, body: ApiError | null = null) {
    super(message)
    this.name = 'HttpError'
    this.status = status
    this.body = body
  }
}

type RequestOptions = Omit<RequestInit, 'body'> & {
  body?: unknown
}

/**
 * Parses an error response body when present.
 */
const parseErrorBody = async (response: Response): Promise<ApiError | null> => {
  const contentType = response.headers.get('content-type')

  if (!contentType?.includes('application/json')) {
    return null
  }

  try {
    const json: unknown = await response.json()
    const parsed = apiErrorSchema.safeParse(json)

    return parsed.success ? parsed.data : null
  } catch {
    return null
  }
}

/**
 * Base fetch wrapper for typed API calls with consistent error handling.
 */
export const apiClient = async <T>(
  path: string,
  options: RequestOptions = {},
): Promise<T> => {
  const { body, headers, ...rest } = options

  const response = await fetch(path, {
    ...rest,
    headers: {
      'Content-Type': 'application/json',
      ...headers,
    },
    body: body === undefined ? undefined : JSON.stringify(body),
  })

  if (!response.ok) {
    const errorBody = await parseErrorBody(response)
    const message = errorBody?.message ?? `Request failed with status ${response.status}`

    throw new HttpError(response.status, message, errorBody)
  }

  if (response.status === 204) {
    return undefined as T
  }

  return (await response.json()) as T
}
