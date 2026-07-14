import {
  createOrderRequestSchema,
  orderPageSchema,
  orderSchema,
  type CreateOrderRequest,
  type Order,
  type OrderPage,
} from '../types'
import { apiClient } from './client'

export type OrderListParams = {
  page?: number
  size?: number
}

export const orderKeys = {
  all: ['orders'] as const,
  lists: () => [...orderKeys.all, 'list'] as const,
  list: (params: OrderListParams) => [...orderKeys.lists(), params] as const,
  details: () => [...orderKeys.all, 'detail'] as const,
  detail: (id: number) => [...orderKeys.details(), id] as const,
}

const buildQueryString = (params: OrderListParams): string => {
  const searchParams = new URLSearchParams()

  if (params.page !== undefined) {
    searchParams.set('page', String(params.page))
  }

  if (params.size !== undefined) {
    searchParams.set('size', String(params.size))
  }

  const query = searchParams.toString()

  return query ? `?${query}` : ''
}

/**
 * Fetches a paginated order history.
 */
export const fetchOrders = async (params: OrderListParams = {}): Promise<OrderPage> => {
  const data = await apiClient<unknown>(`/api/v1/orders${buildQueryString(params)}`)

  return orderPageSchema.parse(data)
}

/**
 * Fetches a single order with line items.
 */
export const fetchOrder = async (id: number): Promise<Order> => {
  const data = await apiClient<unknown>(`/api/v1/orders/${id}`)

  return orderSchema.parse(data)
}

/**
 * Submits a checkout request and returns the created order.
 */
export const createOrder = async (request: CreateOrderRequest): Promise<Order> => {
  const body = createOrderRequestSchema.parse(request)
  const data = await apiClient<unknown>('/api/v1/orders', {
    method: 'POST',
    body,
  })

  return orderSchema.parse(data)
}

export const ordersApi = {
  basePath: '/api/v1/orders',
  fetchOrders,
  fetchOrder,
  createOrder,
}
