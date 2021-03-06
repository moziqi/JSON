package cn.monica.exam.activity;

import java.util.List;

import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.annotation.view.ViewInject;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import cn.monica.exam.ExamApplication;
import com.luojunrong.R;
import cn.monica.exam.entity.Problems;
import cn.monica.exam.service.ProblemService;
import cn.monica.exam.utils.WarnUtils;

public class ErrJudgeActivity  extends FinalActivity implements OnClickListener{
	@ViewInject(id=R.id.txtFillNum) TextView txtNum;
	@ViewInject(id=R.id.txtFillContent) TextView txtContent;
	@ViewInject(id=R.id.txtFillAnswer) TextView txtAnswer;
	@ViewInject(id=R.id.btnA) Button btnRight;
	@ViewInject(id=R.id.btnB) Button btnError;
	@ViewInject(id=R.id.btnFillAdd) Button btnAdd;
	@ViewInject(id=R.id.btnFillLook) Button btnLook;
	@ViewInject(id=R.id.btnLast) Button btnLast;
	@ViewInject(id=R.id.btnNext) Button btnNext;

	//数据库操作
	private ProblemService proService;
	//查找出的所有填空题
	private List<Problems> proList;
	//记录此时的题目顺序号
	private int nowIndex;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//设置标题
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_judge);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.activity_title_common);        
		initTitleBar();

		initData();
		initFontSize();
	}

	private void initData(){
		nowIndex = 0;
		proService = new ProblemService(this);
		proList = proService.getErrorData("2");

		if(null == proList) {
			WarnUtils.toast(this, "没有数据内容！");
			finish();
		} else {
			txtNum.setText("第1题");
			txtContent.setText(proList.get(0).getTitle());
			txtAnswer.setText("答案：" + proList.get(0).getAnswer());
			txtAnswer.setVisibility(View.GONE);
		}		

		btnRight.setOnClickListener(this);
		btnError.setOnClickListener(this);
		btnAdd.setOnClickListener(this);
		btnLook.setOnClickListener(this);
		btnLast.setOnClickListener(this);
		btnNext.setOnClickListener(this);
	}
	
	private void initFontSize(){
		int fontSize = ExamApplication.fontSize;
		txtContent.setTextSize(fontSize);
		txtAnswer.setTextSize(fontSize);
		btnRight.setTextSize(fontSize);
		btnError.setTextSize(fontSize);
	}

	/**
	 * 设置左右标签,专门设置title_bar
	 */
	private void initTitleBar() {
		ImageView leftButton = (ImageView)findViewById(R.id.left_btn);
		leftButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		TextView titleText = (TextView)findViewById(R.id.title);
		titleText.setText(R.string.exam_title);        
		ImageView rightButton = (ImageView)findViewById(R.id.right_btn);
		rightButton.setVisibility(View.GONE);
	}

	private void answerProblem(String choice){
		String answer = proList.get(nowIndex).getAnswer();
		if(choice.equals(answer)){//答对
			proList.get(nowIndex).setFalse(true);
		}else{//答错
			proList.get(nowIndex).setFalse(false);
		}
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.btnA:
			btnRight.setTextColor(this.getResources().getColor(R.color.red));
			btnError.setTextColor(this.getResources().getColor(R.color.black));
			answerProblem(v.getTag().toString());
			break;
		case R.id.btnB:
			btnRight.setTextColor(this.getResources().getColor(R.color.black));
			btnError.setTextColor(this.getResources().getColor(R.color.red));
			answerProblem(v.getTag().toString());
			break;
		case R.id.btnFillAdd:
			boolean flag = proService.AddFavourite(proList.get(nowIndex).getId(), "2");
			if(flag){
				WarnUtils.toast(this, "收藏成功！");
			} else {
				WarnUtils.toast(this, "您已经收藏过了！");
			}
			break;
		case R.id.btnFillLook:
			txtAnswer.setVisibility(View.VISIBLE);
			break;
		case R.id.btnLast:
			if(nowIndex <= 0) {
				WarnUtils.toast(this, "已经是第一题了！");
			}else{
				nowIndex --;
				if(nowIndex == 0){
					String num = "第1题";	
					showText(num, proList.get(0));
					btnNext.setText("下一题");
				}else{
					String num = "第" + (nowIndex + 1) + "题";	
					showText(num, proList.get(nowIndex));
					btnNext.setText("下一题");
				}				
			}
			break;
		case R.id.btnNext:
			if(nowIndex >= (proList.size() - 1)) {
				//showErrorDialog();
			}else{
				nowIndex++;
				int size = proList.size() - 1;
				if(nowIndex == size) {
					String num = "第" + (nowIndex + 1) + "题";				
					showText(num, proList.get(size));
					btnNext.setText("看完了");
				}else{
					String num = "第" + (nowIndex + 1) + "题";				
					showText(num, proList.get(nowIndex));
					btnNext.setText("下一题");
				}				
			}
			break;
		default:
			break;
		}
	}

	private void showText(String num, Problems pro){
		txtNum.setText(num);
		txtContent.setText(pro.getTitle());
		txtAnswer.setText("答案：" + pro.getAnswer());
		txtAnswer.setVisibility(View.GONE);
		btnRight.setTextColor(this.getResources().getColor(R.color.black));
		btnError.setTextColor(this.getResources().getColor(R.color.black));
	}
}

