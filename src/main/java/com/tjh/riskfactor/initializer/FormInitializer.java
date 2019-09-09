package com.tjh.riskfactor.initializer;

import com.tjh.riskfactor.entity.form.Section;
import com.tjh.riskfactor.repo.SectionRepository;
import lombok.val;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import com.tjh.riskfactor.entity.form.Question;
import com.tjh.riskfactor.entity.form.QuestionType;
import com.tjh.riskfactor.repo.QuestionRepository;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class FormInitializer extends BaseInitializer {

    private final QuestionRepository questions;
    private final SectionRepository sections;

    @Override
    protected void initialize() {
        makeBasicInfo();
    }

    private Question question(String fieldName, Consumer<Question> modifiers) {
        val entity = questions.findByFieldName(fieldName);
        return entity.orElseGet(() -> {
            val qu = new Question().setFieldName(fieldName);
            modifiers.accept(qu);
            return questions.save(qu);
        });
    }

    private Question list(String fieldName, QuestionType type, String[][] pairs, Consumer<Question> modifiers) {
        return question(fieldName, qu -> {
            qu.setType(type);
            // why can't you properly deduce?
            Stream<Question> content = Arrays.stream(pairs).map(entry -> {
                val entryFieldName = String.format("%s_%s", fieldName, entry[0]);
                return question(entryFieldName, q -> q.setType(QuestionType.CHOICE).setDescription(entry[1]));
            });
            qu.setList(content.collect(Collectors.toList()));
            modifiers.accept(qu);
        });
    }

    private Section section(String title, List<Question> qlist) {
        return sections.findByTitle(title).orElseGet(() -> {
            val sec = new Section().setTitle(title).setQuestions(qlist);
            return sections.save(sec);
        });
    }

    private Section makeBasicInfo() {
        val centerId = question("centerId", q -> q.setType(QuestionType.IMMUTABLE)
                .setDescription("中心编号"));
        val surveyId = question("surveyId", q -> q.setType(QuestionType.IMMUTABLE)
                .setDescription("研究编号"));

        val birth = question("birth", q -> q.setType(QuestionType.DATE)
                .setDescription("出生日期"));

        val genders = list("gender", QuestionType.SINGLE_CHOICE, new String[][] {
                { "male", "男" },
                { "female", "女" }
        }, q -> q.setDescription("性别"));

        val ethnics = list("ethnic", QuestionType.SINGLE_SELECT, new String[][] {
                { "hanzu", "汉族" },
                { "manzu", "满族" },
                { "mengguzu", "蒙古族" },
                { "huizu", "回族" },
                { "zangzu", "藏族" },
                { "weiwuerzu", "维吾尔族" },
                { "miaozu", "苗族" },
                { "yizu", "彝族" },
                { "zhuangzu", "壮族" },
                { "buyizu", "布依族" },
                { "dongzu", "侗族" },
                { "yaozu", "瑶族" },
                { "baizu", "白族" },
                { "tujiazu", "土家族" },
                { "hanizu", "哈尼族" },
                { "hasakezu", "哈萨克族" },
                { "daizu", "傣族" },
                { "lizu", "黎族" },
                { "lisuzu", "傈僳族" },
                { "wazu", "佤族" },
                { "shezu", "畲族" },
                { "gaoshanzu", "高山族" },
                { "lahuzu", "拉祜族" },
                { "shuizu", "水族" },
                { "dongxiangzu", "东乡族" },
                { "naxizu", "纳西族" },
                { "jingpozu", "景颇族" },
                { "keerkezizu", "柯尔克孜族" },
                { "tuzu", "土族" },
                { "dawoerzu", "达斡尔族" },
                { "mulaozu", "仫佬族" },
                { "qiangzu", "羌族" },
                { "bulangzu", "布朗族" },
                { "salazu", "撒拉族" },
                { "maonanzu", "毛南族" },
                { "gelaozu", "仡佬族" },
                { "xibozu", "锡伯族" },
                { "achangzu", "阿昌族" },
                { "pumizu", "普米族" },
                { "chaoxianzu", "朝鲜族" },
                { "tajikezu", "塔吉克族" },
                { "nuzu", "怒族" },
                { "wuzibiekezu", "乌孜别克族" },
                { "eluosizu", "俄罗斯族" },
                { "ewenkezu", "鄂温克族" },
                { "deangzu", "德昂族" },
                { "baoanzu", "保安族" },
                { "yuguzu", "裕固族" },
                { "jingzu", "京族" },
                { "tataerzu", "塔塔尔族" },
                { "dulongzu", "独龙族" },
                { "elunchunzu", "鄂伦春族" },
                { "hezhezu", "赫哲族" },
                { "menbazu", "门巴族" },
                { "luobazu", "珞巴族" },
                { "jinuozu", "基诺族" }
        }, q -> q.setDescription("民族"));

        val birthPlace = list("birthPlace", QuestionType.LIST, new String[][] {
                { "province", "省" },
                { "city", "市" },
        }, q -> q.setDescription("籍贯"));

        val address = list("address", QuestionType.LIST, new String[][] {
                { "province", "省" },
                { "city", "市" },
                { "county", "县" }
        }, q -> q.setDescription("籍贯"));

        val job = list("job", QuestionType.SINGLE_SELECT, new String[][] {
                { "retired", "退（离）休人员" },
                { "official", "国家公务员" },
                { "expert", "专业技术人员" },
                { "employee", "职员" },
                { "manager", "企业管理人员" },
                { "worker", "工人" },
                { "farmer", "农民" },
                { "student", "学生" },
                { "active_duty", "现役军人" },
                { "self_employed", "自由职业者" },
                { "jobless", "无业人员" },
                { "other", "其他" }
        }, q -> q.setDescription("职业"));

        val height = question("height", q -> q.setType(QuestionType.NUMBER)
                .setDescription("身高"));
        val weight = question("weight", q -> q.setType(QuestionType.NUMBER)
                .setDescription("体重"));

        return section("一般资料", Arrays.asList(centerId, surveyId, birth, genders, ethnics,
                birthPlace, job, address, height, weight));
    }

    private Question duration(String fieldName) {
        val durationUnitField = String.format("%s_%s", fieldName, "duration_unit");
        val durationField = String.format("%s_%s", fieldName, "duration");

        val duration = question(durationField, q -> q.setType(QuestionType.NUMBER)
                .setDescription("持续时间"));
        val durationUnit = list(durationUnitField, QuestionType.SINGLE_SELECT, new String[][]{
                { "day", "天" },
                { "month", "月" },
                { "year", "年" }
        }, q -> q.setDescription("持续时间单位"));


    }

    private Section makeCurrentMedicalHistory() {
        val treatmentTime = question("treatmentTime", q -> q.setType(QuestionType.DATE)
                .setDescription("就诊或入院日期"));
        val onsetTime = question("onsetTime", q -> q.setType(QuestionType.DATE)
                .setDescription("发病时间"));

        val feverPersistTime = question("fever_persist_time", q -> q.setType(QuestionType.DATE).setDescription("持续时间"));
        val fever = question("fever", q -> q.setType(QuestionType.ENABLER));
        val treatmentSymptoms = question("treatmentSymptoms", q -> q.setType(QuestionType.MULTI_CHOICE));

        return section("现病史", Arrays.asList(treatmentTime, onsetTime, treatmentSymptoms));
    }

}
