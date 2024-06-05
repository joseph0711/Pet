package com.example.pet.ui.healthcare;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.pet.ArticleAdapter;
import com.example.pet.ArticleClass;
import com.example.pet.R;
import com.example.pet.databinding.FragmentHealthCareBinding;

import java.util.ArrayList;
import java.util.List;

public class HealthCareFragment extends Fragment {
    private @NonNull FragmentHealthCareBinding binding;
    private RecyclerView recyclerView;
    private ArticleAdapter articleAdapter;
    private List<ArticleClass> articleClassList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHealthCareBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        recyclerView = root.findViewById(R.id.healthCare_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        // Dummy data
        articleClassList = new ArrayList<>();
        articleClassList.add(new ArticleClass("狗狗日常疾病及處理", "Joseph Chiang", "05-21-2024", "此篇文章將闡述狗狗日常疾病及處理方式。",
                "## 本篇重點\n" +
                        "- 日常疾病\n" +
                        "- 疾病預防\n" +
                        "- 注意事項\n" +
                        "\n" +
                        "## 日常疾病\n" +
                        "1. 犬傳染性肝炎：幼犬感染居多。主要是侵害肝細胞與內皮細胞。\n" +
                        "* 症狀：厭食、精神不振、發燒嘔吐、下痢和軟便，有些會在發病後12～24小時呈現藍眼症。\n" +
                        "\n" +
                        "2. 狂犬病：狂犬病是最重要的人畜共通疾病之一，由病毒感染，病毒經傷口周遭神經抵達中樞神經遍布全身，可感染所有哺乳動物，包括人類、犬、貓等，死亡率高。\n" +
                        "* 症狀：行為異常，瞳孔初期放大、後期縮小，咽喉逐漸麻痺，吠叫聲改變。\n" +
                        "\n" +
                        "3. 犬瘟熱：犬瘟熱俗稱麻疹，是死亡率很高的傳染病，病毒從感染犬隻的分泌物，經由空氣、直接或間接傳播。\n" +
                        "* 症狀：發燒、無食慾、精神不振、眼屎多、流鼻涕、咳嗽、肝炎、下痢、血便。\n" +
                        "\n" +
                        "4. 犬冠狀病毒腸炎：該病是一種高度傳染性的腸道疾病，藉糞便、嘔吐物、被排泄物污染的食物器皿來傳播。\n" +
                        "* 症狀：間歇性嘔吐、下痢、厭食、輕微發燒、抑鬱、排便惡臭並為淡橘色。\n" +
                        "\n" +
                        "5. 犬傳染性支氣管炎：又名哮喘或犬舍咳，屬呼吸道疾病，發病原因為環境因素包括濕度過高、寒流等氣候變化，該病潛伏期約5～10天。\n" +
                        "* 症狀：劇烈乾咳、反胃、嘔吐，嚴重者會嗜睡、發燒、食慾不振及肺炎，甚至死亡。\n" +
                        "\n" +
                        "## 疾病預防\n" +
                        "1. 透過接種疫苗的方式可預防感染犬瘟熱，因此幼犬在適當年齡必需接受疫苗接種\n" +
                        "2. 做好環境衛生，把所有狗玩具及用徹底消毒\n" +
                        "3. 以胸背袋取代頸圈，減少壓迫喉嚨及氣管引起患犬咳嗽。\n" +
                        "4. 不要讓寵物吃容易中毒的食物，例如提子、朱古力等，並確保寵物有清潔的飲用水，也要別讓寵物暴露在有化學成份的環境下，例如殺蟲劑、蚊香、消毒藥水等含有除蟲菊配方的化學物質。\n" +
                        "5. 常常幫狗刷洗牙齒，牙齒疾病也是慢性腎衰竭最常見的原因之一。因此保持寵物良好的口腔健康非常重要。\n" +
                        "6. 定期做身體檢查，監測寵物的食慾、飲水量和精神狀態等。\n" +
                        "7. 避免帶狗狗於溪澗玩耍或飲用溪水，接近水道時用狗繩牽引狗狗\n" +
                        "\n" +
                        "## 注意事項\n" +
                        "1. 除了日常散步外，根據狗狗的品種和能量水平，確保提供足夠的運動，如慢跑、游泳、追逐球等活動。\n" +
                        "2. 為狗狗提供安靜、穩定的生活環境，避免過度吵鬧或緊張的情況，減輕狗狗的壓力\n" +
                        "3. 要進行室內大小便訓練，首先需要準備一個方便清潔的空間，例如廁所或陽台等地方，在這個空間內鋪滿報紙或尿布墊，作為狗狗的專用廁所。\n" +
                        "4. 小型犬注意環境，避免由高處跌落\n" +
                        "5. 提供足夠的玩具，避免狗狗感到無聊，減少破壞行為\n" +
                        "6. 為狗狗佩戴項圈並附上身份牌，內含聯繫方式，考慮植入晶片。\n" +
                        "7. 避免給狗狗食用巧克力、葡萄、洋蔥、蒜等對狗狗有害的食物\n" +
                        "8. 根據狗狗的毛髮長度和種類，定時打梳毛髮，防止結繞\n" +
                        "\n" +
                        "## 參考資料\n" +
                        "- [狗狗健康主人要顧！盤點12種常見疾病和如何預防](https://health.udn.com/health/story/123123/5405699)\n" +
                        "\n" +
                        "- [12個狗狗常見疾病  常見症狀及預防方法](https://goodbyedear.com.hk/12%E5%80%8B%E7%8B%97%E7%8B%97%E5%B8%B8%E8%A6%8B%E7%96%BE%E7%97%85-%E5%B8%B8%E8%A6%8B%E7%97%87%E7%8B%80%E5%8F%8A%E9%A0%90%E9%98%B2%E6%96%B9%E6%B3%95/)"));
        articleClassList.add(new ArticleClass("貓咪日常疾病及處理", "Joseph Chiang", "05-21-2024", "此篇文章將闡述貓咪日常疾病及處理方式。",
                "## 本篇重點\n" +
                        "- 日常疾病\n" +
                        "- 疾病預防\n" +
                        "- 注意事項\n" +
                        "\n" +
                        "## 日常疾病\n" +
                        "1. 泌尿道疾病：泌尿道疾病是很容易在短時間內對貓咪生命造成威脅的疾病！尤其當病情發展到有尿道結石或是泌尿道完全堵塞時更為危險，如果在72小時內不及時治療，貓咪容易死亡。\n" +
                        "* 症狀：尿在貓砂盆外、不斷舔生殖器、排尿困難、排尿時會不進食、不喝水、嗜睡、血尿。\n" +
                        "\n" +
                        "2. 胃炎：引發胃炎的原因很多，可能是吃到不新鮮食物、吃東西速度過快、吃到會過敏的食物或細菌感染…等。\n" +
                        "* 症狀：嘔吐、打嗝、食慾不振、血便、腹瀉。\n" +
                        "\n" +
                        "3. 腎功能衰竭：貓咪腎衰竭與犬瘟熱疫苗接踵及長期吃乾糧有關係，由於貓咪腎衰竭的初期症狀並不明顯，且通常有明顯的症狀表現時，貓咪幾乎已經喪失一半以上的腎功能，所以建議飼主最好讓貓咪接受定期血液檢查來追蹤內臟健康數據，以免錯失黃金治療期。\n" +
                        "* 症狀：容易口渴、頻尿、流口水、口臭。\n" +
                        "\n" +
                        "4. 腸道炎症：患病原因有飲食突然改變、吃到有毒物質、吃到會過敏的食物、細菌感染、寄生蟲感染或因腎臟病併發。\n" +
                        "* 症狀：腹瀉、食慾不振、嘔吐。\n" +
                        "\n" +
                        "5. 皮膚過敏：引發皮膚過敏的原因可能有食物過敏、跳蚤、花粉、螨及黴菌。\n" +
                        "* 症狀：不斷抓或啃咬患部、患部出現皮疹、患部掉毛。\n" +
                        "\n" +
                        "6. 糖尿病：超重的貓咪容易罹患糖尿病，貓咪糖尿病的治療跟人一樣，需要每天控制飲食及運動，並且根據貓咪的需求，提供口服藥或注射劑來減緩病情發展。\n" +
                        "* 症狀：肥胖、容易口渴、頻尿、尿在貓砂盆外、嗜睡、憂鬱。\n" +
                        "\n" +
                        "## 疾病預防\n" +
                        "1. 增加水分攝取。\n" +
                        "2. 控制飲食，貓年紀大時不宜攝取過多蛋白質，鈉的攝取也不宜過多，鉀攝取不足也可能導致慢性腎臟病。\n" +
                        "3. 別讓貓咪吃到有毒物質，像是地板非無毒清潔劑或發霉飼料，都會造成腎臟損害\n" +
                        "4. 高溫和高濕度會影響貓的免疫力，居家溫度宜設在28度以下、濕度控制在50％左右。\n" +
                        "5. 施打基本核心疫苗，幼貓從8～9週齡開始施打，接下來每3～4週補強，最後一劑疫苗落在16週齡以上（共三劑）。若是從未施打過的成貓，應施打兩劑核心疫苗，相隔3～4週。小時候打過疫苗、超過五年沒有補強的老貓，也需要補強一劑。\n" +
                        "\n" +
                        "## 注意事項\n" +
                        "1. 貓咪常吐毛，可以餵食化毛膏或貓草。\n" +
                        "2. 可以勤梳毛，避免貓咪吞入太多毛。\n" +
                        "3. 要注意結紮時期。\n" +
                        "4. 避免公貓發情逃家，就算結紮也要注意，因為貓咪前幾年也可能會保有未結紮時的習慣。\n" +
                        "5. 春夏之際是體外寄生蟲猖獗的時期，要特別注意驅蟲。\n" +
                        "6. 注意食碗、水碗、貓砂盆的清潔，避免發霉。\n" +
                        "7. 注意貓咪飲水狀態，不要讓貓咪缺水。\n" +
                        "8. 若有必要可以幫貓咪剃毛，但建議先詢問獸醫。\n" +
                        "9. 容易著涼感冒，要特別注意貓皰疹。\n" +
                        "10. 可能食慾不振，要注意飲食量。\n" +
                        "\n" +
                        "## 參考資料\n" +
                        "- [喵星人的健康殺手！　10大貓咪常見疾病整理](https://health.ettoday.net/news/756134)\n" +
                        "\n" +
                        "- [新手養貓須知：超詳細養貓注意事項指南（日用品、飲食禁忌、日常護理、不同季節狀態）](https://blog.petdaddy.com.tw/keep-cat-tips/)"));

        articleAdapter = new ArticleAdapter(getContext(), articleClassList);
        recyclerView.setAdapter(articleAdapter);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}