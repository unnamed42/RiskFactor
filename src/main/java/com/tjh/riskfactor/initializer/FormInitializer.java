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
        makeCurrentMedicalHistory();
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
        val found = sections.findByTitle("一般资料");
        if(found.isPresent())
            return found.get();

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
                { "han", "汉族" },
                { "man", "满族" },
                { "menggu", "蒙古族" },
                { "hui", "回族" },
                { "zang", "藏族" },
                { "weiwuer", "维吾尔族" },
                { "miao", "苗族" },
                { "yi", "彝族" },
                { "zhuang", "壮族" },
                { "buyi", "布依族" },
                { "dong", "侗族" },
                { "yao", "瑶族" },
                { "bai", "白族" },
                { "tujia", "土家族" },
                { "hani", "哈尼族" },
                { "hasake", "哈萨克族" },
                { "dai", "傣族" },
                { "li", "黎族" },
                { "lisu", "傈僳族" },
                { "wa", "佤族" },
                { "she", "畲族" },
                { "gaoshan", "高山族" },
                { "lahu", "拉祜族" },
                { "shui", "水族" },
                { "dongxiang", "东乡族" },
                { "naxi", "纳西族" },
                { "jingpo", "景颇族" },
                { "keerkezi", "柯尔克孜族" },
                { "tu", "土族" },
                { "dawoer", "达斡尔族" },
                { "mulao", "仫佬族" },
                { "qiang", "羌族" },
                { "bulang", "布朗族" },
                { "sala", "撒拉族" },
                { "maonan", "毛南族" },
                { "gelao", "仡佬族" },
                { "xibo", "锡伯族" },
                { "achang", "阿昌族" },
                { "pumi", "普米族" },
                { "chaoxian", "朝鲜族" },
                { "tajike", "塔吉克族" },
                { "nu", "怒族" },
                { "wuzibieke", "乌孜别克族" },
                { "eluosi", "俄罗斯族" },
                { "ewenke", "鄂温克族" },
                { "deang", "德昂族" },
                { "baoan", "保安族" },
                { "yugu", "裕固族" },
                { "jing", "京族" },
                { "tataer", "塔塔尔族" },
                { "dulong", "独龙族" },
                { "elunchun", "鄂伦春族" },
                { "hezhe", "赫哲族" },
                { "menba", "门巴族" },
                { "luoba", "珞巴族" },
                { "jinuo", "基诺族" }
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

        return duration;
    }

    private Section makeCurrentMedicalHistory() {
        val found = sections.findByTitle("现病史");
        if(found.isPresent())
            return found.get();

        val treatmentTime = question("treatmentTime", q -> q.setType(QuestionType.DATE)
                .setDescription("就诊或入院日期"));
        val onsetTime = question("onsetTime", q -> q.setType(QuestionType.DATE)
                .setDescription("发病时间"));

        val feverPersistTime = question("fever_persist_time", q -> q.setType(QuestionType.DATE).setDescription("持续时间"));
        val fever = question("fever", q -> q.setType(QuestionType.ENABLER).setDescription("发烧"));
        val treatmentSymptoms = question("treatmentSymptoms", q -> q.setType(QuestionType.MULTI_CHOICE).setDescription("就诊症状"));

        return section("现病史", Arrays.asList(treatmentTime, onsetTime, treatmentSymptoms));
    }

}
