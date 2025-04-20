"use client"

import type * as React from "react"
import { Circle } from "lucide-react"
import { Line, ResponsiveContainer, Tooltip, XAxis, YAxis, LineChart } from "recharts"

import { cn } from "@/lib/utils"

export interface ChartConfig {
  [key: string]: {
    label?: string
    color?: string
    icon?: React.ComponentType<{ className?: string }>
  }
}

export interface ChartData {
  [key: string]: string | number | Date
}

export interface ChartProps extends React.HTMLAttributes<HTMLDivElement> {
  config: ChartConfig
  data: ChartData[]
  xKey?: string
  yKeys?: string[]
}

export function ChartContainer({ config, data, xKey = "timestamp", yKeys, className, ...props }: ChartProps) {
  const keys = yKeys || Object.keys(config)

  return (
    <div className={cn("space-y-4 rounded-lg border p-4", className)} {...props}>
      <div className="h-[200px]">
        <ResponsiveContainer width="100%" height="100%">
          <LineChart data={data} margin={{ top: 5, right: 10, left: 10, bottom: 0 }}>
            <XAxis dataKey={xKey} tickLine={false} axisLine={false} minTickGap={30} />
            <YAxis tickLine={false} axisLine={false} width={40} />
            <Tooltip content={<CustomTooltip config={config} />} />
            {keys.map((key) => (
              <Line
                key={key}
                type="monotone"
                dataKey={key}
                stroke={config[key]?.color || "#0ea5e9"}
                strokeWidth={2}
                dot={false}
                activeDot={{ r: 6 }}
                isAnimationActive={false}
              />
            ))}
          </LineChart>
        </ResponsiveContainer>
      </div>
    </div>
  )
}

interface TooltipPayloadItem {
  dataKey: string
  name?: string
  value: number
  payload: ChartData
  color?: string
  fill?: string
  stroke?: string
}

interface CustomTooltipProps {
  active?: boolean
  payload?: TooltipPayloadItem[]
  label?: string
  config: ChartConfig
}

function CustomTooltip({ active, payload, label, config }: CustomTooltipProps) {
  if (!active || !payload?.length) {
    return null
  }

  return (
    <div className="rounded-lg border bg-background p-2 shadow-sm">
      <div className="grid gap-0.5">
        <div className="text-xs text-muted-foreground">{label}</div>
      </div>
      <div className="grid gap-2 pt-2">
        {payload.map((item, index) => {
          const dataKey = item.dataKey
          const color = item.color || item.fill || item.stroke || "#0ea5e9"
          const name = config[dataKey]?.label || dataKey
          const value = item.value

          return (
            <div key={index} className="flex items-center justify-between gap-2">
              <div className="flex items-center gap-1">
                <Circle className="h-2 w-2" style={{ color }} />
                <span className="text-xs font-medium">{name}</span>
              </div>
              <div className="text-xs font-medium">{value}</div>
            </div>
          )
        })}
      </div>
    </div>
  )
}

export interface ChartTooltipProps extends React.HTMLAttributes<HTMLDivElement> {
  active?: boolean
  payload?: TooltipPayloadItem[]
  label?: string
  hideLabel?: boolean
}

export function ChartTooltip({
  active,
  payload,
  label,
  hideLabel = false,
  className,
  children,
  ...props
}: ChartTooltipProps) {
  if (!active || !payload?.length) {
    return null
  }

  return (
    <div className={cn("rounded-lg border bg-background p-2 shadow-sm", className)} {...props}>
      {!hideLabel && (
        <div className="grid gap-0.5">
          <div className="text-xs text-muted-foreground">{label}</div>
        </div>
      )}
      {children}
    </div>
  )
}

export interface ChartTooltipContentProps extends React.HTMLAttributes<HTMLDivElement> {
  payload?: TooltipPayloadItem[]
  hideLabel?: boolean
}

export function ChartTooltipContent({ payload, hideLabel = false, className, ...props }: ChartTooltipContentProps) {
  if (!payload?.length) {
    return null
  }

  return (
    <div className={cn("grid gap-2", hideLabel ? "" : "pt-2", className)} {...props}>
      {payload.map((item, index) => {
        const dataKey = item.dataKey
        const color = item.color || item.fill || item.stroke || "#0ea5e9"
        const name = item.name || dataKey
        const value = item.value

        return (
          <div key={index} className="flex items-center justify-between gap-2">
            <div className="flex items-center gap-1">
              <Circle className="h-2 w-2" style={{ color }} />
              <span className="text-xs font-medium">{name}</span>
            </div>
            <div className="text-xs font-medium">{value}</div>
          </div>
        )
      })}
    </div>
  )
}
