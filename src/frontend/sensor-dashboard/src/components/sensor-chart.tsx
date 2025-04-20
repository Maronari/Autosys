"use client"

import { ChartContainer, type ChartData } from "@/components/ui/chart"

type SensorChartProps = {
  data: { timestamp: number; value: number }[]
  unit: string
}

export default function SensorChart({ data, unit }: SensorChartProps) {
  // Форматируем данные для графика
  const chartData: ChartData[] = data.map((point) => ({
    timestamp: new Date(point.timestamp).toLocaleTimeString(),
    value: point.value,
  }))

  return (
    <ChartContainer
      config={{
        value: {
          label: `Значение (${unit})`,
          color: "hsl(var(--chart-1))",
        },
      }}
      data={chartData}
      xKey="timestamp"
      yKeys={["value"]}
      className="h-[200px]"
    />
  )
}
