-- fieldName命名规则：
-- * 本身应该是camelCase的命名风格
-- * 以下划线分隔成不同部分，比如说Section 1里的address问题的province项目，命名应当为 1_address_province
--     如果实在难以取名，可以直接赋予一个唯一字符串

-- ID编号：
-- * 问题从1开始，问题下属子问题从100000开始，子问题的子问题跟随100000递增编号；问题选项的ID编号规则相同
-- * 问题选项与问题的ID分开编号，不要求它们ID相同
-- * 分属不同部分的问题/问题选项的ID要划分开，下一组的ID取下一个整十位数，比如编号到11了下一组从20开始

-- 用IDE写的时候去掉注释，免得到处报错
-- mdzz的spring一定要改分号分隔符才能创建触发器，我佛了
-- delimiter ^;

drop trigger if exists `fieldname_null_to_uuid` ^;
create trigger `fieldname_null_to_uuid` before insert on question
for each row begin
    if new.field_name is null then
        set new.field_name = uuid_short();
    end if;
end ^;

-- 让第二次执行脚本时立即产生错误，然后退出
create table if not exists exec_guard(id int primary key)^;
insert into exec_guard value (0)^;

-- 一般资料部分
insert into question_option(id, error_message, filter_key, placeholder_text, required) values
    (1, '请输入出生日期', null, null, true),
    (2, '请选择性别', null, '性别', true),
    (3, '请选择民族', 'key', '输入拼音可筛选', true),
    (4, '请选择职业', null, null, true),
    (5, '请输入身高', null, '身高', true),
    (6, '请输入体重', null, '体重', true)^;
insert into question(id, description, field_name, `type`, option_id) values
    (1, '中心编号', '1_centerId', 'IMMUTABLE', null),
    (2, '研究编号', '1_surveyId', 'IMMUTABLE', null),
    (3, '出生日期', '1_birth', 'DATE', 1),
    (4, '性别', '1_gender', 'SINGLE_SELECT', 2),
    (5, '民族', '1_ethnic', 'SINGLE_SELECT', 3),
    (6, '籍贯', '1_birthPlace', 'LIST', null),
    (7, '职业', '1_job', 'SINGLE_SELECT', 4),
    (8, '通讯地址', '1_address', 'LIST', null),
    (9, '身高（单位：米）', '1_height', 'NUMBER', 5),
    (10, '体重（单位：米）', '1_weight', 'NUMBER', 6)^;
insert into section(id, title) values
    (1, '一般资料')^;
insert into section_question_list(sid, qid, sequence) values
    (1, 1, 0),
    (1, 2, 1),
    (1, 3, 2),
    (1, 4, 3),
    (1, 5, 4),
    (1, 6, 5),
    (1, 7, 6),
    (1, 8, 7),
    (1, 9, 8),
    (1, 10, 9)^;
-- 一般资料部分，子问题
insert into question_option(id, filter_key, error_message, required) values
    (100000, 'han', null, false),
    (100001, 'man', null, false),
    (100002, 'menggu', null, false),
    (100003, 'hui', null, false),
    (100004, 'zang', null, false),
    (100005, 'weiwuer', null, false),
    (100006, 'miao', null, false),
    (100007, 'yi', null, false),
    (100008, 'zhuang', null, false),
    (100009, 'buyi', null, false),
    (100010, 'dong', null, false),
    (100011, 'yao', null, false),
    (100012, 'bai', null, false),
    (100013, 'tujia', null, false),
    (100014, 'hani', null, false),
    (100015, 'hasake', null, false),
    (100016, 'dai', null, false),
    (100017, 'li', null, false),
    (100018, 'lisu', null, false),
    (100019, 'wa', null, false),
    (100020, 'she', null, false),
    (100021, 'gaoshan', null, false),
    (100022, 'lahu', null, false),
    (100023, 'shui', null, false),
    (100024, 'dongxiang', null, false),
    (100025, 'naxi', null, false),
    (100026, 'jingpo', null, false),
    (100027, 'keerkezi', null, false),
    (100028, 'tu', null, false),
    (100029, 'dawoer', null, false),
    (100030, 'mulao', null, false),
    (100031, 'qiang', null, false),
    (100032, 'bulang', null, false),
    (100033, 'sala', null, false),
    (100034, 'maonan', null, false),
    (100035, 'gelao', null, false),
    (100036, 'xibo', null, false),
    (100037, 'achang', null, false),
    (100038, 'pumi', null, false),
    (100039, 'chaoxian', null, false),
    (100040, 'tajike', null, false),
    (100041, 'nu', null, false),
    (100042, 'wuzibieke', null, false),
    (100043, 'eluosi', null, false),
    (100044, 'ewenke', null, false),
    (100045, 'deang', null, false),
    (100046, 'baoan', null, false),
    (100047, 'yugu', null, false),
    (100048, 'jing', null, false),
    (100049, 'tataer', null, false),
    (100050, 'dulong', null, false),
    (100051, 'elunchun', null, false),
    (100052, 'hezhe', null, false),
    (100053, 'menba', null, false),
    (100054, 'luoba', null, false),
    (100055, 'jinuo', null, false),
    --
    (100056, null, '请输入籍贯省', true),
    (100057, null, '请输入籍贯市', true),
    --
    (100058, null, '请输入省', true),
    (100059, null, '请输入市', true),
    (100060, null, '请输入县', true)^;
insert into question(id, description, `type`, option_id) values
    (100000, '男', 'CHOICE', null),
    (100001, '女', 'CHOICE', null),
    --
    (100002, '汉族', 'CHOICE', 100000),
    (100003, '满族', 'CHOICE', 100001),
    (100004, '蒙古族', 'CHOICE', 100002),
    (100005, '回族', 'CHOICE', 100003),
    (100006, '藏族', 'CHOICE', 100004),
    (100007, '维吾尔族', 'CHOICE', 100005),
    (100008, '苗族', 'CHOICE', 100006),
    (100009, '彝族', 'CHOICE', 100007),
    (100010, '壮族', 'CHOICE', 100008),
    (100011, '布依族', 'CHOICE', 100009),
    (100012, '侗族', 'CHOICE', 100010),
    (100013, '瑶族', 'CHOICE', 100011),
    (100014, '白族', 'CHOICE', 100012),
    (100015, '土家族', 'CHOICE', 100013),
    (100016, '哈尼族', 'CHOICE', 100014),
    (100017, '哈萨克族', 'CHOICE', 100015),
    (100018, '傣族', 'CHOICE', 100016),
    (100019, '黎族', 'CHOICE', 100017),
    (100020, '傈僳族', 'CHOICE', 100018),
    (100021, '佤族', 'CHOICE', 100019),
    (100022, '畲族', 'CHOICE', 100020),
    (100023, '高山族', 'CHOICE', 100021),
    (100024, '拉祜族', 'CHOICE', 100022),
    (100025, '水族', 'CHOICE', 100023),
    (100026, '东乡族', 'CHOICE', 100024),
    (100027, '纳西族', 'CHOICE', 100025),
    (100028, '景颇族', 'CHOICE', 100026),
    (100029, '柯尔克孜族', 'CHOICE', 100027),
    (100030, '土族', 'CHOICE', 100028),
    (100031, '达斡尔族', 'CHOICE', 100029),
    (100032, '仫佬族', 'CHOICE', 100030),
    (100033, '羌族', 'CHOICE', 100031),
    (100034, '布朗族', 'CHOICE', 100032),
    (100035, '撒拉族', 'CHOICE', 100033),
    (100036, '毛南族', 'CHOICE', 100034),
    (100037, '仡佬族', 'CHOICE', 100035),
    (100038, '锡伯族', 'CHOICE', 100036),
    (100039, '阿昌族', 'CHOICE', 100037),
    (100040, '普米族', 'CHOICE', 100038),
    (100041, '朝鲜族', 'CHOICE', 100039),
    (100042, '塔吉克族', 'CHOICE', 100040),
    (100043, '怒族', 'CHOICE', 100041),
    (100044, '乌孜别克族', 'CHOICE', 100042),
    (100045, '俄罗斯族', 'CHOICE', 100043),
    (100046, '鄂温克族', 'CHOICE', 100044),
    (100047, '德昂族', 'CHOICE', 100045),
    (100048, '保安族', 'CHOICE', 100046),
    (100049, '裕固族', 'CHOICE', 100047),
    (100050, '京族', 'CHOICE', 100048),
    (100051, '塔塔尔族', 'CHOICE', 100049),
    (100052, '独龙族', 'CHOICE', 100050),
    (100053, '鄂伦春族', 'CHOICE', 100051),
    (100054, '赫哲族', 'CHOICE', 100052),
    (100055, '门巴族', 'CHOICE', 100053),
    (100056, '珞巴族', 'CHOICE', 100054),
    (100057, '基诺族', 'CHOICE', 100055),
    --
    (100058, '省', 'TEXT', 100056),
    (100059, '市', 'TEXT', 100057),
    --
    (100060, '省', 'TEXT', 100058),
    (100061, '市', 'TEXT', 100059),
    (100062, '县', 'TEXT', 100060),
    --
    (100063, '退（离）休人员', 'CHOICE', null),
    (100064, '国家公务员', 'CHOICE', null),
    (100065, '专业技术人员', 'CHOICE', null),
    (100066, '职员', 'CHOICE', null),
    (100067, '企业管理人员', 'CHOICE', null),
    (100068, '工人', 'CHOICE', null),
    (100069, '农民', 'CHOICE', null),
    (100070, '学生', 'CHOICE', null),
    (100071, '现役军人', 'CHOICE', null),
    (100072, '自由职业者', 'CHOICE', null),
    (100073, '无业人员', 'CHOICE', null),
    (100074, '其他', 'CHOICE', null)^;
insert into question_list(head, qid, sequence) values
    (4, 100000, 0),
    (4, 100001, 1),
    --
    (5, 100002, 0),
    (5, 100003, 1),
    (5, 100004, 2),
    (5, 100005, 3),
    (5, 100006, 4),
    (5, 100007, 5),
    (5, 100008, 6),
    (5, 100009, 7),
    (5, 100010, 8),
    (5, 100011, 9),
    (5, 100012, 10),
    (5, 100013, 11),
    (5, 100014, 12),
    (5, 100015, 13),
    (5, 100016, 14),
    (5, 100017, 15),
    (5, 100018, 16),
    (5, 100019, 17),
    (5, 100020, 18),
    (5, 100021, 19),
    (5, 100022, 20),
    (5, 100023, 21),
    (5, 100024, 22),
    (5, 100025, 23),
    (5, 100026, 24),
    (5, 100027, 25),
    (5, 100028, 26),
    (5, 100029, 27),
    (5, 100030, 28),
    (5, 100031, 29),
    (5, 100032, 30),
    (5, 100033, 31),
    (5, 100034, 32),
    (5, 100035, 33),
    (5, 100036, 34),
    (5, 100037, 35),
    (5, 100038, 36),
    (5, 100039, 37),
    (5, 100040, 38),
    (5, 100041, 39),
    (5, 100042, 40),
    (5, 100043, 41),
    (5, 100044, 42),
    (5, 100045, 43),
    (5, 100046, 44),
    (5, 100047, 45),
    (5, 100048, 46),
    (5, 100049, 47),
    (5, 100050, 48),
    (5, 100051, 49),
    (5, 100052, 50),
    (5, 100053, 51),
    (5, 100054, 52),
    (5, 100055, 53),
    (5, 100056, 54),
    (5, 100057, 55),
    --
    (6, 100058, 0),
    (6, 100059, 1),
    --
    (8, 100060, 0),
    (8, 100061, 1),
    (8, 100062, 2),
    --
    (7, 100063, 0),
    (7, 100064, 1),
    (7, 100065, 2),
    (7, 100066, 3),
    (7, 100067, 4),
    (7, 100068, 5),
    (7, 100069, 6),
    (7, 100070, 7),
    (7, 100071, 8),
    (7, 100072, 9),
    (7, 100073, 10),
    (7, 100074, 11)^;

-- 现病史部分
insert into question_option(id, error_message, additional_description, required) values
    (10, '请输入就诊或入院日期', null, true),
    (11, '请输入发病时间', '精确到年，月份或日期未知用1月1日代替', true)^;
insert into question(id, description, field_name, `type`, option_id) values
    (20, '就诊或入院日期', 'treatmentTime', 'DATE', 10),
    (21, '发病时间', 'onsetTime', 'DATE', 11),
    (22, '就诊症状', 'symptoms', 'MULTI_CHOICE', null),
    (23, '入院体征', 'treatmentSymptoms', 'MULTI_CHOICE', null)^;
insert into section(id, title) values
    (2, '现病史')^;
insert into section_question_list(sid, qid, sequence) values
    (2, 20, 0),
    (2, 21, 1),
    (2, 22, 2),
    (2, 23, 3)^;
-- 现病史部分，子问题
insert into question_option(id, is_enabler, prefix_postfix, default_selected, required, error_message, placeholder_text) values
    (100070, true, null, null, false, null, null),
    (100071, false, null, null, true, '请输入持续时间', null),
    (100072, false, false, 0, false, null, null),
    (100073, false, null, null, true, '请输入症状', '症状名称')^;
insert into question(id, description, field_name, `type`, option_id) values
    (100070, '腹痛', null, 'CHOICE', 100070),
    (100071, '腹胀', null, 'CHOICE', 100070),
    (100072, '黄疸', null, 'CHOICE', 100070),
    (100073, '恶心呕吐', null, 'CHOICE', 100070),
    (100074, '乏力纳差', null, 'CHOICE', 100070),
    (100075, '消瘦', null, 'CHOICE', 100070),
    (100076, '发热', null, 'CHOICE', 100070),
    (100077, '其他', null, 'CHOICE', 100070),
    -- 附带联动，病症持续时间
    (100078, '持续时间', 'symptoms_stomachache_duration', 'NUMBER', 100071),
    (100079, null, 'symptoms_stomachache_unit', 'SINGLE_SELECT', 100072),
    (100080, '持续时间', 'symptoms_bloating_duration', 'NUMBER', 100071),
    (100081, null, 'symptoms_bloating_unit', 'SINGLE_SELECT', 100072),
    (100082, '持续时间', 'symptoms_jaundice_duration', 'NUMBER', 100071),
    (100083, null, 'symptoms_jaundice_unit', 'SINGLE_SELECT', 100072),
    (100084, '持续时间', 'symptoms_vomit_duration', 'NUMBER', 100071),
    (100085, null, 'symptoms_vomit_unit', 'SINGLE_SELECT', 100072),
    (100086, '持续时间', 'symptoms_weakness_duration', 'NUMBER', 100071),
    (100087, null, 'symptoms_weakness_unit', 'SINGLE_SELECT', 100072),
    (100088, '持续时间', 'symptoms_emaciate_duration', 'NUMBER', 100071),
    (100089, null, 'symptoms_emaciate_unit', 'SINGLE_SELECT', 100072),
    (100090, '持续时间', 'symptoms_fever_duration', 'NUMBER', 100071),
    (100091, null, 'symptoms_fever_unit', 'SINGLE_SELECT', 100072),
    (100092, '持续时间', 'symptoms_other_duration', 'NUMBER', 100071),
    (100093, null, 'symptoms_other_unit', 'SINGLE_SELECT', 100072),
    -- “其他”的联动内容
    (100094, null, 'symptoms_other_name', 'TEXT', 100073),
    -- 共用的时间单位选项
    (100095, '天', 'duration_unit_d', 'CHOICE', null),
    (100096, '月', 'duration_unit_m', 'CHOICE', null),
    (100097, '年', 'duration_unit_y', 'CHOICE', null),
    -- 入院体征
    (100098, '腹部包块', 'ts_abdominalMass', 'CHOICE', null),
    (100099, '黄疸', 'ts_jaundice', 'CHOICE', null),
    (100100, 'Murphy征', 'ts_murphy', 'CHOICE', null),
    (100101, '肝大', 'ts_hepatomegaly', 'CHOICE', null),
    (100102, '上腹压痛', 'ts_abdominalTenderness', 'CHOICE', null),
    (100103, '腹水', 'ts_ascites', 'CHOICE', null),
    (100104, '心率不齐', 'ts_arrhythmia', 'CHOICE', null),
    (100105, '发热', 'ts_fever', 'CHOICE', null),
    (100106, '其他', 'ts_other', 'CHOICE', 100073),
    -- 入院体征“其他”的联动内容
    (100107, null, 'ts_other_name', 'TEXT', null)^;
insert into question_list(head, qid, sequence) values
    (22, 100070, 0),
    (22, 100071, 1),
    (22, 100072, 2),
    (22, 100073, 3),
    (22, 100074, 4),
    (22, 100075, 5),
    (22, 100076, 6),
    (22, 100077, 7),
    -- 联动内容
    (100070, 100078, 0),
    (100070, 100079, 1),
    (100071, 100080, 0),
    (100071, 100081, 1),
    (100072, 100082, 0),
    (100072, 100083, 1),
    (100073, 100084, 0),
    (100073, 100085, 1),
    (100074, 100086, 0),
    (100074, 100087, 1),
    (100075, 100088, 0),
    (100075, 100089, 1),
    (100076, 100090, 0),
    (100076, 100091, 1),
    (100077, 100094, 0), -- “其他”的病症名输入
    (100077, 100092, 1),
    (100077, 100093, 2),
    -- 时间select
    (100079, 100095, 0),
    (100079, 100096, 1),
    (100079, 100097, 2),
    (100081, 100095, 0),
    (100081, 100096, 1),
    (100081, 100097, 2),
    (100083, 100095, 0),
    (100083, 100096, 1),
    (100083, 100097, 2),
    (100085, 100095, 0),
    (100085, 100096, 1),
    (100085, 100097, 2),
    (100087, 100095, 0),
    (100087, 100096, 1),
    (100087, 100097, 2),
    (100089, 100095, 0),
    (100089, 100096, 1),
    (100089, 100097, 2),
    (100091, 100095, 0),
    (100091, 100096, 1),
    (100091, 100097, 2),
    (100093, 100095, 0),
    (100093, 100096, 1),
    (100093, 100097, 2),
    -- 入院体征
    (23, 100098, 0),
    (23, 100099, 1),
    (23, 100100, 2),
    (23, 100101, 3),
    (23, 100102, 4),
    (23, 100103, 5),
    (23, 100104, 6),
    (23, 100105, 7),
    (23, 100106, 8),
    (100106, 100107, 0)^;

-- 既往病史部分
insert into question_option(id, additional_description, is_enabler) values
    (20, '有/无', true),
    (21, '有/无', false)^;
insert into question(id, description, field_name, `type`, option_id) values
    (30, '胆囊炎', 'cholecystitis', 'YESNO_CHOICE', 20),
    (31, '胆囊结石', 'gallstone', 'YESNO_CHOICE', 20),
    (32, '胆囊息肉', 'gallbladderPolyps', 'YESNO_CHOICE', 20),
    (33, '胆管结石', 'bileDuctStone', 'YESNO_CHOICE', 20),
    (34, 'Mirrizi综合征', 'mirriziSyndrome', 'YESNO_CHOICE', 21),
    (35, '胆道系统感染', 'biliarySystemInfection', 'YESNO_CHOICE', 21),
    (36, '寄生虫病', 'parasiticDisease', 'YESNO_CHOICE', 20),
    (37, '胆胰管汇合异常', 'pancreaticobiliaryConfluence', 'YESNO_CHOICE', 21),
    (38, '心脑血管疾病', 'cardiovascularDisease', 'YESNO_CHOICE', 20),
    (39, '手术史', 'historySurgery', 'LIST_APPENDABLE', null),
    (40, '其他', 'mh_other', 'LIST_APPENDABLE', null)^;
insert into section(id, title) values
    (3, '既往病史')^;
insert into section_question_list(sid, qid, sequence) values
    (3, 30, 0),
    (3, 31, 1),
    (3, 32, 2),
    (3, 33, 3),
    (3, 34, 4),
    (3, 35, 5),
    (3, 36, 6),
    (3, 37, 7),
    (3, 38, 8),
    (3, 39, 9),
    (3, 40, 10)^;
-- 既往病史，子问题
insert into question_option(id, required, error_message, additional_description) values
    (100100, true, '请选择急性/慢性', null),
    (100101, true, '请填写大小', '大小请填写二维值，单位：毫米'),
    (100102, true, '请选择发作次数', null),
    (100103, true, '请选择结石类型', null),
    (100104, true, '请选择寄生虫类型', null),
    (100105, true, '请选择心脑血管疾病类型', null),
    (100106, true, '请选择手术时间', '精确到年，如月份和日期未知，用1月或1日代替'),
    (100107, true, '请输入手术名称', null)^;
insert into question(id, description, field_name, `type`, option_id) values
    (100110, null, 'cholecystitis_severity', 'SINGLE_CHOICE', 100100),
    --
    (100111, '急性', 'cholecystitis_acute', 'CHOICE', null),
    (100112, '慢性', 'cholecystitis_chronic', 'CHOICE', null),
    --
    (100113, null, 'gallstone_info', 'LIST', null),
    (100114, null, 'gallbladderPolyps_info', 'LIST', null),
    (100115, '单发', 'gall_attackOnce', 'CHOICE', null),
    (100116, '多发', 'gall_attackMultiple', 'CHOICE', null),
    (100117, '大小', 'gall_size', 'NUMBER', 100101),
    (100118, null, 'gallstone_info_freq', 'SINGLE_CHOICE', 100102),
    (100119, null, 'gallbladderPolyps_info_freq', 'SINGLE_CHOICE', 100102),
    --
    (100120, null, 'bileDuctStone_info', 'SINGLE_SELECT', 100103),
    (100121, '肝内胆管结石', 'bileDuctStone_1', 'CHOICE', null),
    (100122, '肝外胆管结石', 'bileDuctStone_2', 'CHOICE', null),
    --
    (100123, null, 'parasite_type', 'MULTI_CHOICE', 100104),
    (100124, '胆道蛔虫', 'parasite_type_1', 'CHOICE', null),
    (100125, '华支睾吸虫', 'parasite_type_2', 'CHOICE', null),
    --
    (100126, null, 'cardiovascular_type', 'MULTI_CHOICE', 100105),
    (100127, '高血压', 'cardiovascular_type_1', 'CHOICE', null),
    (100128, '糖尿病', 'cardiovascular_type_2', 'CHOICE', null),
    (100129, '其他', 'cardiovascular_other', 'TEXT', null),
    --
    (100130, '手术时间', 'historySurgery_date', 'DATE', 100106),
    (100131, '手术名称', 'historySurgery_name', 'TEXT', 100107),
    (100132, null, 'historySurgery_template', 'LIST', null)^;
insert into question_list(head, qid, sequence) values
    (100110, 100111, 0),
    (100110, 100112, 1),
    (30, 100110, 0),
    -- 单发/多发，内容一样，共享选项
    (100118, 100115, 0),
    (100118, 100116, 1),
    (100119, 100115, 0),
    (100119, 100116, 1),
    (100113, 100118, 0),
    (100113, 100117, 1),
    (31, 100113, 0),
    (100114, 100119, 0),
    (100114, 100117, 1),
    (32, 100114, 0),
    --
    (100120, 100121, 0),
    (100120, 100122, 1),
    (33, 100120, 0),
    --
    (100123, 100124, 0),
    (100123, 100125, 1),
    (36, 100123, 0),
    --
    (100126, 100127, 0),
    (100126, 100128, 1),
    (100126, 100129, 3),
    (38, 100126, 0),
    --
    (100132, 100130, 0),
    (100132, 100131, 1),
    (39, 100132, 0)^;

-- 个人史部分
insert into question_option(id, additional_description, is_enabler) values
    (20, '是/否', true),
    (21, 'expr:weight/(height*height)', false)^;
insert into question(id, description, field_name, `type`, option_id) values
    (30, '吸烟', 'smoking', 'YESNO_CHOICE', 20),
    (31, '饮酒', 'drinking', 'YESNO_CHOICE', 20),
    (32, '饮水', 'water', 'SINGLE_CHOICE', null),
    (33, '饮食', 'meal', 'SINGLE_SELECT', null),
    (34, 'BMI', 'bmi', 'IMMUTABLE', 21)^;
insert into section(id, title) values
    (4, '既往病史')^;
insert into section_question_list(sid, qid, sequence) values
    (4, 30, 0),
    (4, 31, 1),
    (4, 32, 2),
    (4, 33, 3),
    (4, 34, 4)^;
-- 个人史子问题
insert into question_option(id, error_message, required, prefix_postfix) values
    (100110, '请输入数字', true, false)^;
insert into question(id, description, field_name, `type`, option_id) values
    (100140, '支/天', 'smoking_count', 'NUMBER', 100110),
    (100141, '年', 'smoking_duration', 'NUMBER', 100110),
    (100141, '两/天', 'drinking_count', 'NUMBER', 100110),
    (100142, '年', 'drinking_duration', 'NUMBER', 100110),
    (100143, null, 'smoking_info', 'LIST', null),
    (100144, null, 'drinking_info', 'LIST', null)^;
insert into question_list(head, qid, sequence) values
    (100143, 100140, 0),
    (100143, 100141, 1),
    (30, 100143, 0),
    --
    (100144, 100141, 0),
    (100144, 100142, 1),
    (31, 100144, 0)^;

-- 家族史
-- insert into question_option(id, additional_description, is_enabler) values
--     (30, '有/无', true),
--     ^;
-- insert into question(id, description, field_name, `type`, option_id) values
--     (40, '肿瘤', 'cancer', 'YESNO_CHOICE', 20),
--     (41, '胆道疾病', 'biliaryDisease', 'YESNO_CHOICE', 20),
--     (42, '心血管疾病', 'familyCardiovascularDisease', 'YESNO_CHOICE', 21)^;
-- insert into section(id, title) values
--     (5, '家族史')^;
-- insert into section_question_list(sid, qid, sequence) values
--     (5, 40, 0),
--     (5, 41, 1),
--     (5, 42, 2)^;
-- -- 家族史子问题
-- insert into question_option(id, additional_description, is_enabler)
