import { z } from 'zod'

/** API error payload returned by the backend. */
export const apiErrorSchema = z.object({
  code: z.string().optional(),
  message: z.string(),
  fieldErrors: z
    .array(
      z.object({
        field: z.string(),
        message: z.string(),
      }),
    )
    .optional(),
})

export type ApiError = z.infer<typeof apiErrorSchema>

/** Health check response from `/api/v1/health`. */
export const healthResponseSchema = z.object({
  status: z.string(),
})

export type HealthResponse = z.infer<typeof healthResponseSchema>

/** Product catalog item (placeholder for upcoming product endpoints). */
export const productSchema = z.object({
  id: z.number(),
  name: z.string(),
  sku: z.string(),
  price: z.number(),
  stockQuantity: z.number(),
  active: z.boolean(),
})

export type Product = z.infer<typeof productSchema>

/** Paginated product list response (placeholder). */
export const productPageSchema = z.object({
  content: z.array(productSchema),
  totalElements: z.number(),
  totalPages: z.number(),
  size: z.number(),
  number: z.number(),
})

export type ProductPage = z.infer<typeof productPageSchema>

/** Order status values. */
export const orderStatusSchema = z.enum(['PENDING', 'CONFIRMED', 'CANCELLED'])

export type OrderStatus = z.infer<typeof orderStatusSchema>

/** Order line item (placeholder). */
export const orderItemSchema = z.object({
  id: z.number(),
  productId: z.number(),
  quantity: z.number(),
  unitPrice: z.number(),
})

export type OrderItem = z.infer<typeof orderItemSchema>

/** Order summary (placeholder). */
export const orderSchema = z.object({
  id: z.number(),
  status: orderStatusSchema,
  totalAmount: z.number(),
  createdAt: z.string(),
  items: z.array(orderItemSchema).optional(),
})

export type Order = z.infer<typeof orderSchema>
