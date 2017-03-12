package kr.co.topcredu.chartapplication;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.LinearLayout;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

public class LineChartActivity extends AppCompatActivity {

    //막대그래프이 가로축
    private String [] mMonth = new String[] {
            "Jan", "Feb" , "Mar" , "Apr","May" ,"Jun","Jul","Aug","Sep","Oct","Nov","Dec"
    };

    private GraphicalView mChartView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line_chart);
        drawChart();

    }

    private void drawChart(){
        int [] x = {1,2,3,4,5,6,7,8};
        int [] income = { 2000,25000,23000,2900,
                1500,6800,4900,5900};
        int [] expense = {1500,2400,2600,3800,
                1600,1400,2700,9000};
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
        multiRenderer.setZoomButtonsVisible(true);
        for(int i =0 ;i<x.length;i++){
            multiRenderer.addXTextLabel(i+1,mMonth[i]);

        }

        multiRenderer.addSeriesRenderer(incomeRenderer);
        multiRenderer.addSeriesRenderer(expenseRenderer);

        if(mChartView ==null){
            LinearLayout layout = (LinearLayout) findViewById(R.id.chart_line);
            mChartView = ChartFactory.getLineChartView(this, dataset,multiRenderer);
            multiRenderer.setClickEnabled(true);
            multiRenderer.setSelectableBuffer(10);
            layout.addView(mChartView, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,
                    LinearLayout.LayoutParams.FILL_PARENT));
        } else {
            mChartView.repaint();
        }
    }

}
