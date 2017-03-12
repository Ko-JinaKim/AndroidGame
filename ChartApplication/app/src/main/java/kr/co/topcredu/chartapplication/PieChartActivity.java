package kr.co.topcredu.chartapplication;

import android.graphics.Color;
import android.icu.util.ULocale;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.LinearLayout;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.CategorySeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;

public class PieChartActivity extends AppCompatActivity {

    int [] pieChartValues = {10,10,20,20,40};
    public static final String TYPE = "type";
    // 각 계열의 색상
    private static int [] COLORS = new int [] {Color.YELLOW,Color.GREEN,Color.BLUE,Color.MAGENTA,Color.CYAN };
    // 각 계열의 타이틀
    String[] mSeriesTitle = new String[]{"PIE1","PIE2","PIE3","PIE4","PIE5"};

    private CategorySeries mSeries = new CategorySeries("계열");
    private DefaultRenderer mRenderer = new DefaultRenderer();
    private GraphicalView mChartView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pie_chart);

        mRenderer.setApplyBackgroundColor(true);
        mRenderer.setBackgroundColor(Color.argb(100,50,50,50));
        mRenderer.setChartTitleTextSize(20);
        mRenderer.setLabelsTextSize(30);
        mRenderer.setLegendTextSize(30);
        mRenderer.setMargins(new int[]{20,30,15,0});
        mRenderer.setZoomButtonsVisible(true);
        mRenderer.setStartAngle(90);

        if(mChartView == null){
            LinearLayout layout = (LinearLayout) findViewById(R.id.chart_Pie);
            mChartView = ChartFactory.getPieChartView(this, mSeries,mRenderer);
            mRenderer.setClickEnabled(true);
            mRenderer.setSelectableBuffer(10);
            layout.addView(mChartView,new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,
                    LinearLayout.LayoutParams.FILL_PARENT));
        } else {
            mChartView.repaint();
        }
        fillPieChart();
    }

    public void fillPieChart(){
        for (int i=0; i< pieChartValues.length;i++){
            mSeries.add(mSeriesTitle[i] +"_"+(String.valueOf(pieChartValues[i])),pieChartValues[i]);
            // chart 에서 사용할 값, 색깔, 텍스트 등을 defaultRender 객체에서 설정
            SimpleSeriesRenderer renderer = new SimpleSeriesRenderer();
            renderer.setColor(COLORS[(mSeries.getItemCount()-1) % COLORS.length]);
            mRenderer.addSeriesRenderer(renderer);

            if(mChartView != null)
                mChartView.repaint();
        }
    }




}
