package cn.monica.exam.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import cn.monica.exam.adapter.IListViewCallBack;
import cn.monica.exam.adapter.ListViewAdapter;
import cn.monica.exam.entity.WenJuan;
import cn.monica.exam.entity.Word;
import cn.monica.exam.utils.ActivityUtils;
import cn.monica.exam.utils.HttpUrlConstacts;
import cn.monica.exam.utils.HttpUtil;
import com.luojunrong.R;
import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.annotation.view.ViewInject;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class WordListActivity extends FinalActivity{

	@ViewInject(id=R.id.exam_list)
	ListView listView;
	List<Word> list;
	int project_id;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_exam);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.activity_title_common);


		initTitleBar();

		initViews();
	}


	private void initViews(){
		Bundle extras = getIntent().getExtras();
		project_id = extras.getInt("PROJECT_ID");
		HashMap<String, String> param = new HashMap<String, String>();
		param.put("typeId", project_id + "");
		HttpUtil.NetWorkDoGetAsyncTask doget = new HttpUtil.NetWorkDoGetAsyncTask(HttpUrlConstacts.getHost()+HttpUrlConstacts.URL_GET_WORD,param , new HttpUtil.NetWorkCallBack() {
			@Override
			public void onSuccess(String jsonDate) {
				try {
					JSONObject data = new JSONObject(jsonDate);
					JSONObject page = data.getJSONObject("page");
					JSONArray wordList = page.getJSONArray("content");
					 list = new ArrayList<Word>();
					for(int i = 0 ; i < wordList.length();i++){
						JSONObject temp = wordList.getJSONObject(i);
						Word word = new Word(temp.toString());
						list.add(word);
					}

					listView.setAdapter(new ListViewAdapter<Word>(WordListActivity.this, list, new IListViewCallBack<Word>() {
						@Override
						public View getView(Context context, int position, View convertView, List<Word> list) {
							convertView = LayoutInflater.from(context).inflate(R.layout.project_listview_item, null);
							TextView tv = (TextView) convertView.findViewById(R.id.project_item_tv);
							Word word = list.get(position);
							tv.setText(word.title);
							return convertView;
						}
					}));
					listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
						@Override
						public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
							Bundle data = new Bundle();
							Word word = list.get(position);
							data.putSerializable("WORD_DATA",word);
							ActivityUtils.to(WordListActivity.this, WordActivity.class,data);
						}
					});
				} catch (JSONException e) {
					e.printStackTrace();
				}

			}

			@Override
			public void onError(String msg) {

			}
		});
		doget.execute();
	}


	private void initTitleBar() {
		ImageView leftButton = (ImageView)findViewById(R.id.left_btn);
		leftButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		TextView titleText = (TextView)findViewById(R.id.title);
		titleText.setText(R.string.study_title);
		ImageView rightButton = (ImageView)findViewById(R.id.right_btn);
		rightButton.setVisibility(View.GONE);
	}
}
