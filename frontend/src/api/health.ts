import { apiClient } from './client'
import { healthResponseSchema, type HealthResponse } from '../types'

/**
 * Fetches API health status.
 */
export const getHealth = async (): Promise<HealthResponse> => {
  const data = await apiClient<unknown>('/api/v1/health')

  return healthResponseSchema.parse(data)
}
