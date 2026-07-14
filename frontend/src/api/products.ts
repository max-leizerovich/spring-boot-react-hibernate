import {
  productPageSchema,
  productSchema,
  type Product,
  type ProductPage,
} from '../types'
import { apiClient } from './client'

export type ProductListParams = {
  page?: number
  size?: number
  search?: string
  active?: boolean
}

export const productKeys = {
  all: ['products'] as const,
  lists: () => [...productKeys.all, 'list'] as const,
  list: (params: ProductListParams) => [...productKeys.lists(), params] as const,
  details: () => [...productKeys.all, 'detail'] as const,
  detail: (id: number) => [...productKeys.details(), id] as const,
}

const buildQueryString = (params: ProductListParams): string => {
  const searchParams = new URLSearchParams()

  if (params.page !== undefined) {
    searchParams.set('page', String(params.page))
  }

  if (params.size !== undefined) {
    searchParams.set('size', String(params.size))
  }

  if (params.search) {
    searchParams.set('search', params.search)
  }

  if (params.active !== undefined) {
    searchParams.set('active', String(params.active))
  }

  const query = searchParams.toString()

  return query ? `?${query}` : ''
}

/**
 * Fetches a paginated product catalog.
 */
export const fetchProducts = async (params: ProductListParams = {}): Promise<ProductPage> => {
  const data = await apiClient<unknown>(`/api/v1/products${buildQueryString(params)}`)

  return productPageSchema.parse(data)
}

/**
 * Fetches a single product by id.
 */
export const fetchProduct = async (id: number): Promise<Product> => {
  const data = await apiClient<unknown>(`/api/v1/products/${id}`)

  return productSchema.parse(data)
}

export const productsApi = {
  basePath: '/api/v1/products',
  fetchProducts,
  fetchProduct,
}
