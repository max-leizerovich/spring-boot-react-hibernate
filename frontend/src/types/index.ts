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

/** Order line item returned by the API. */
export const orderItemSchema = z.object({
  id: z.number(),
  productId: z.number(),
  quantity: z.number(),
  unitPrice: z.number(),
})

export type OrderItem = z.infer<typeof orderItemSchema>

/** Order summary or detail returned by the API. */
export const orderSchema = z.object({
  id: z.number(),
  status: orderStatusSchema,
  totalAmount: z.number(),
  createdAt: z.string(),
  items: z.array(orderItemSchema).nullable(),
})

export type Order = z.infer<typeof orderSchema>

/** Paginated order list response. */
export const orderPageSchema = z.object({
  content: z.array(orderSchema),
  totalElements: z.number(),
  totalPages: z.number(),
  size: z.number(),
  number: z.number(),
})

export type OrderPage = z.infer<typeof orderPageSchema>

/** Checkout line item request body. */
export const createOrderItemRequestSchema = z.object({
  productId: z.number(),
  quantity: z.number().int().min(1),
})

export type CreateOrderItemRequest = z.infer<typeof createOrderItemRequestSchema>

/** Checkout request body. */
export const createOrderRequestSchema = z.object({
  items: z.array(createOrderItemRequestSchema).min(1),
})

export type CreateOrderRequest = z.infer<typeof createOrderRequestSchema>

/** Client-side cart line with product snapshot for display. */
export const cartItemSchema = z.object({
  productId: z.number(),
  name: z.string(),
  sku: z.string(),
  price: z.number(),
  quantity: z.number().int().min(1),
  stockQuantity: z.number().int().min(0),
})

export type CartItem = z.infer<typeof cartItemSchema>
