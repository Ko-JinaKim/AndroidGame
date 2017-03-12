package kr.co.topcredu.chartapplication;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.LinearLayout;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.BarChart;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

public class BarChartActivity extends AppCompatActivity {

    private GraphicalView mChartView;
    private String [] mMonth = new String[]{
            "Jan", "Feb" , "Mar" , "Apr","May" ,"Jun","Jul","Aug","Sep","Oct","Nov","Dec"
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bar_chart);
        drawChart();

    }

    private void drawChart(){
        int [] x = {1,2,3,4,5,6,7,8,9,10,11};
        int [] income = { 2000,25000,23000,2900,
                1500,6800,4900,5900,6100,5400,2800};
        int [] expense = {1500,2400,2600,3800,
                1600,1400,2700,9000,8400,2600,3500};


        //Creating on XYSeries for Income
        XYSeries incomeSeries = new XYSeries("수입");
        // Creating on XYSeries for Expense
        XYSeries expenseSeries = new XYSeries("지출");
        // Adding data to Income and Expense Series]

        for(int i=0;i<x.length;i++){
            incomeSeries.add(x[i],income[i]);
            expenseSeries.add(x[i],expense[i]);
        }// for end

        //creating a dataset to hold each series
        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();

        //Adding Income Series to the dataset
        dataset.addSeries(incomeSeries);

        //Adding Expense Series to dataset
        dataset.addSeries(expenseSeries);

        //Creating XYSeriesRender to customize incomeSeries
        XYSeriesRenderer incomeRenderer = new XYSeriesRenderer();
        incomeRenderer.setColor(Color.LTGRAY);
        incomeRenderer.setPointStyle(PointStyle.DIAMOND);
        incomeRenderer.setFillPoints(true);
        incomeRenderer.setLineWidth(1);
        incomeRenderer.setDisplayChartValuesDistance(10);
        incomeRenderer.setDisplayChartValues(true);


        //creating xySeriesRenderer to customixe expenseries

        XYSeriesRenderer expenseRenderer = new XYSeriesRenderer();
        expenseRenderer.setColor(Color.BLUE);
        expenseRenderer.setPointStyle(PointStyle.TRIANGLE);
        expenseRenderer.setFillPoints(true);
        expenseRenderer.setLineWidth(2);
        expenseRenderer.setDisplayChartValues(true);


        XYMultipleSeriesRenderer multiRenderer = new XYMultipleSeriesRenderer();
        multiRenderer.setXLabels(0);
        multiRenderer.setChartTitle("수입 vs 지출 Chart");
        multiRenderer.setXTitle("Year 2017");
        multiRenderer.setYTitle("Amount in Dollars");

        multiRenderer.setChartTitleTextSize(28);
        multiRenderer.setAxisTitleTextSize(24);
        multiRenderer.setLabelsTextSize(24);
        multiRenderer.setZoomButtonsVisible(false);
        multiRenderer.setPanEnabled(false,false);
        multiRenderer.setClickEnabled(false);
        multiRenderer.setZoomEnabled(false,false);
        multiRenderer.setShowGridX(true);
        multiRenderer.setShowGridY(true);
        multiRenderer.setFitLegend(true);
        multiRenderer.setShowGrid(false);
        multiRenderer.setZoomEnabled(false);
        multiRenderer.setExternalZoomEnabled(false);
        multiRenderer.setAntialiasing(true);
        multiRenderer.setInScroll(false);
        multiRenderer.setLegendHeight(30);
        multiRenderer.setXLabelsAlign(Paint.Align.CENTER);
        multiRenderer.setYLabelsAlign(Paint.Align.LEFT);
        multiRenderer.setTextTypeface("sans_serif", Typeface.NORMAL);
        multiRenderer.setYLabels(10);
        multiRenderer.setYAxisMax(4000);
        multiRenderer.setXAxisMin(-0.5);
        multiRenderer.setYAxisMax(11);
        multiRenderer.setBarSpacing(0.5);
        multiRenderer.setBackgroundColor(Color.TRANSPARENT);
        multiRenderer.setMarginsColor(Color.GREEN);
        multiRenderer.setApplyBackgroundColor(true);
        multiRenderer.setMargins(new int[] { 30,30,30,30});
        for(int i=0;i<x.length;i++){
            multiRenderer.addXTextLabel(i,mMonth[i]);
        }


        multiRenderer.addSeriesRenderer(incomeRenderer);
        multiRenderer.addSeriesRenderer(expenseRenderer);

        LinearLayout layout = (LinearLayout)findViewById(R.id.chart_bar);
        layout.removeAllViews();

        mChartView = ChartFactory.getBarChartView(this, dataset,multiRenderer, BarChart.Type.DEFAULT);
        layout.addView(mChartView, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,
                LinearLayout.LayoutParams.FILL_PARENT));


























    }
}
