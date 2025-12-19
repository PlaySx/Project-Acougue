import React from 'react';
import { Bar } from 'react-chartjs-2';
import { Chart as ChartJS, CategoryScale, LinearScale, BarElement, Title, Tooltip, Legend } from 'chart.js';
import { Card, CardContent, Typography } from '@mui/material';

ChartJS.register(CategoryScale, LinearScale, BarElement, Title, Tooltip, Legend);

const TopProductsChart = ({ data }) => {
  const chartData = {
    labels: data.map(p => p.productName),
    datasets: [
      {
        label: 'Quantidade Vendida',
        data: data.map(p => p.quantitySold),
        backgroundColor: 'rgba(75, 192, 192, 0.6)',
        borderColor: 'rgba(75, 192, 192, 1)',
        borderWidth: 1,
      },
    ],
  };

  const options = {
    indexAxis: 'y', // Faz o gráfico ser de barras horizontais
    responsive: true,
    plugins: {
      legend: {
        display: false,
      },
      title: {
        display: true,
        text: 'Top 5 Produtos Mais Vendidos',
      },
    },
  };

  return (
    <Card>
      <CardContent>
        <Bar options={options} data={chartData} />
      </CardContent>
    </Card>
  );
};

export default TopProductsChart;
