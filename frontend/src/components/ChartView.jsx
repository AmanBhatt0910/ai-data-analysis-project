import { BarChart, Bar, XAxis, YAxis, Tooltip } from "recharts";

function ChartView({data} ) {
    return (
        <BarChart width={500} height={300} data={data}>
            <XAxis dataKey="name" />
            <YAxis />
            <Tooltip />
            <Bar dataKey="value" />
        </BarChart>
    )
}

export default ChartView;