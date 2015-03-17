package com.swater.meimeng.activity.adapterGeneral.vo;

import java.io.Serializable;
import java.util.List;

import org.json.JSONObject;

import com.meimeng.app.R;
import com.swater.meimeng.mutils.constant.MConstantUser.UserAll_hobby_info;
import com.swater.meimeng.mutils.constant.MConstantUser.UserAll_life_info;

public class UserInfo implements Serializable {

	String header = "";
	String heart_description = "";
	String province_name = "";
	String city_name = "";
	// ----baic-info--
	String nickName = "";
	String age = "";
	String height = "";
	String marriage = "";
	String degree = "";
	String child = "";
	int shield_count = 0;
	int locked = -1;;
	int sex=0;
	String city_id="";
	
	String id_mingzu="";
	String id_nation="";
	String id_oversea="";
	String id_career="";
	String id_shengxiao="";
	String id_xingzuo="";
	String id_family_bg="";
	String id_live="";
	String id_car="";
	String id_assert="";
	String id_salery="";
	
//	---
	
	String id_marriage="";
	String id_degree="";
	String id_child="";
	
	
	
	public String getId_child() {
		return id_child;
	}

	public void setId_child(String id_child) {
		this.id_child = id_child;
	}

	public String getId_marriage() {
		return id_marriage;
	}

	public void setId_marriage(String id_marriage) {
		this.id_marriage = id_marriage;
	}

	public String getId_degree() {
		return id_degree;
	}

	public void setId_degree(String id_degree) {
		this.id_degree = id_degree;
	}

	public String getId_mingzu() {
		return id_mingzu;
	}

	public void setId_mingzu(String id_mingzu) {
		this.id_mingzu = id_mingzu;
	}

	public String getId_nation() {
		return id_nation;
	}

	public void setId_nation(String id_nation) {
		this.id_nation = id_nation;
	}

	public String getId_oversea() {
		return id_oversea;
	}

	public void setId_oversea(String id_oversea) {
		this.id_oversea = id_oversea;
	}

	public String getId_career() {
		return id_career;
	}

	public void setId_career(String id_career) {
		this.id_career = id_career;
	}

	public String getId_shengxiao() {
		return id_shengxiao;
	}

	public void setId_shengxiao(String id_shengxiao) {
		this.id_shengxiao = id_shengxiao;
	}

	public String getId_xingzuo() {
		return id_xingzuo;
	}

	public void setId_xingzuo(String id_xingzuo) {
		this.id_xingzuo = id_xingzuo;
	}

	public String getId_family_bg() {
		return id_family_bg;
	}

	public void setId_family_bg(String id_family_bg) {
		this.id_family_bg = id_family_bg;
	}

	public String getId_live() {
		return id_live;
	}

	public void setId_live(String id_live) {
		this.id_live = id_live;
	}

	public String getId_car() {
		return id_car;
	}

	public void setId_car(String id_car) {
		this.id_car = id_car;
	}

	public String getId_assert() {
		return id_assert;
	}

	public void setId_assert(String id_assert) {
		this.id_assert = id_assert;
	}

	public String getId_salery() {
		return id_salery;
	}

	public void setId_salery(String id_salery) {
		this.id_salery = id_salery;
	}

	public String getCity_id() {
		return city_id;
	}

	public void setCity_id(String city_id) {
		this.city_id = city_id;
	}

	public int getSex() {
		return sex;
	}

	public void setSex(int sex) {
		this.sex = sex;
	}

	String voice_url="";
	String voice_url_audit="";
	String voice_url_local="";

	public String getVoice_url() {
		return voice_url;
	}

	public void setVoice_url(String voice_url) {
		this.voice_url = voice_url;
	}

	public String getVoice_url_audit() {
		return voice_url_audit;
	}

	public void setVoice_url_audit(String voice_url_audit) {
		this.voice_url_audit = voice_url_audit;
	}

	public String getVoice_url_local() {
		return voice_url_local;
	}

	public void setVoice_url_local(String voice_url_local) {
		this.voice_url_local = voice_url_local;
	}

	public int getLocked() {
		return locked;
	}

	/** locked:相册头像上锁状态,int;1-对所有人开放(默认);2-对所有人关闭;3-对部分人开放;个人设置中也会用到这个状态; */
	public void setLocked(int locked) {
		this.locked = locked;
	}

	// ---
	// ----detail--info
	String nationality = "";
	String blood = "";
	String shengxiao = "";
	String xingzuo = "";
	String uid = "";
//	----
	
//	setValueToView(findViewById(R.id.va3_attract_place), va_goodpos);
//	setValueToView(findViewById(R.id.va3_bodystyle), va_bs);
//	setValueToView(findViewById(R.id.va3_eyecolor), va_ec);
//	setValueToView(findViewById(R.id.va3_facestyle), va_fs);
//	setValueToView(findViewById(R.id.va3_hairclor), va_hc);
//	setValueToView(findViewById(R.id.va3_hairstyle), va_hs);
//	setValueToView(findViewById(R.id.va3_self_asert), va_face_self);
//	setValueToView(findViewById(R.id.va3_weight), va_weight);
	String va_goopos="";
	String va_bs="";
	String va_ec="";
	String va_fs="";
	String va_hc="";
	String va_face_self="";
	String va_weight="";
	String va_hs="";
	String json_detailinfo=null;
	String json_bodystyle=null;
	String json_workinfo=null;
	String json_lifeinfo=null;
	String json_base_info=null;
	String json_hobby_info=null;
//	setValueToView(findViewById(R.id.va4_comp_trade), va_cp_trade);
//	setValueToView(findViewById(R.id.va4_comp_type), va_cp_type);
//	setValueToView(findViewById(R.id.va4_grad_year), grad_year);
//	setValueToView(findViewById(R.id.va4_income_desc), income);
//	setValueToView(findViewById(R.id.va4_job_state), va_workstate);
//	setValueToView(findViewById(R.id.va4_lang), lang);
//	setValueToView(findViewById(R.id.va4_prof_type), va_pro);
	String va4_comp_trade="";
	String va4_comp_type="";
	String va4_grad_year="";
	String va4_income_desc="";
	String va4_job_state="";
	String va4_lang="";
	String va4_prof_type="";
	
	String  va5_big_lar="";
	String  va5_drink="";
	String  va5_execise="";
	String  va5_issmoke="";
	String  va5_iswantchild="";
	String  va5_livewith="";
	String  va5_pets="";
	String  va5_rank="";
	String  va5_rest="";
	String  va5_skills="";
	String  va5_zongjiao="";
	String  va5_makeroma="";
	
	
//	getHobbyData(
//			obj_hobby_info
//					.getJSONArray(UserAll_hobby_info.amateur_like),
//			"业余爱好:");
//	getHobbyData(
//			obj_hobby_info
//					.getJSONArray(UserAll_hobby_info.attention_item),
//			"关注节目:");
//	getHobbyData(
//			obj_hobby_info
//					.getJSONArray(UserAll_hobby_info.entertainment),
//			"娱乐休闲:");
//	getHobbyData(
//			obj_hobby_info.getJSONArray(UserAll_hobby_info.like_book),
//			"喜欢的书籍:");
//	getHobbyData(
//			obj_hobby_info.getJSONArray(UserAll_hobby_info.like_food),
//			"喜欢的食物:");
//	getHobbyData(
//			obj_hobby_info.getJSONArray(UserAll_hobby_info.like_movie),
//			"喜欢的电影:");
//	getHobbyData(
//			obj_hobby_info.getJSONArray(UserAll_hobby_info.like_music),
//			"喜欢的音乐:");
//	getHobbyData(
//			obj_hobby_info.getJSONArray(UserAll_hobby_info.like_sports),
//			"喜欢的运动:");
//	getHobbyData(
//			obj_hobby_info.getJSONArray(UserAll_hobby_info.travel),
//			"喜欢的旅游:");
	
	String va6_amateur_like="";
	String va6_attention_item="";
	String va6_entertainment="";
	String va6_like_book="";
	String va6_like_food="";
	String va6_like_movie="";
	String va6_like_music="";
	String va6_like_sports="";
	String va6_like_travel="";
	List<HobbyVo> hobby_data=null;
	

	

	

	public List<HobbyVo> getHobby_data() {
		return hobby_data;
	}

	public void setHobby_data(List<HobbyVo> hobby_data) {
		this.hobby_data = hobby_data;
	}

	public String getVa6_amateur_like() {
		return va6_amateur_like;
	}

	public void setVa6_amateur_like(String va6_amateur_like) {
		this.va6_amateur_like = va6_amateur_like;
	}

	public String getVa6_attention_item() {
		return va6_attention_item;
	}

	public void setVa6_attention_item(String va6_attention_item) {
		this.va6_attention_item = va6_attention_item;
	}

	public String getVa6_entertainment() {
		return va6_entertainment;
	}

	public void setVa6_entertainment(String va6_entertainment) {
		this.va6_entertainment = va6_entertainment;
	}

	public String getVa6_like_book() {
		return va6_like_book;
	}

	public void setVa6_like_book(String va6_like_book) {
		this.va6_like_book = va6_like_book;
	}

	public String getVa6_like_food() {
		return va6_like_food;
	}

	public void setVa6_like_food(String va6_like_food) {
		this.va6_like_food = va6_like_food;
	}

	public String getVa6_like_movie() {
		return va6_like_movie;
	}

	public void setVa6_like_movie(String va6_like_movie) {
		this.va6_like_movie = va6_like_movie;
	}

	public String getVa6_like_music() {
		return va6_like_music;
	}

	public void setVa6_like_music(String va6_like_music) {
		this.va6_like_music = va6_like_music;
	}

	public String getVa6_like_sports() {
		return va6_like_sports;
	}

	public void setVa6_like_sports(String va6_like_sports) {
		this.va6_like_sports = va6_like_sports;
	}

	public String getVa6_like_travel() {
		return va6_like_travel;
	}

	public void setVa6_like_travel(String va6_like_travel) {
		this.va6_like_travel = va6_like_travel;
	}

	public String getVa5_big_lar() {
		return va5_big_lar;
	}

	public void setVa5_big_lar(String va5_big_lar) {
		this.va5_big_lar = va5_big_lar;
	}

	public String getVa5_drink() {
		return va5_drink;
	}

	public void setVa5_drink(String va5_drink) {
		this.va5_drink = va5_drink;
	}

	public String getVa5_execise() {
		return va5_execise;
	}

	public void setVa5_execise(String va5_execise) {
		this.va5_execise = va5_execise;
	}

	public String getVa5_issmoke() {
		return va5_issmoke;
	}

	public void setVa5_issmoke(String va5_issmoke) {
		this.va5_issmoke = va5_issmoke;
	}

	public String getVa5_iswantchild() {
		return va5_iswantchild;
	}

	public void setVa5_iswantchild(String va5_iswantchild) {
		this.va5_iswantchild = va5_iswantchild;
	}

	public String getVa5_livewith() {
		return va5_livewith;
	}

	public void setVa5_livewith(String va5_livewith) {
		this.va5_livewith = va5_livewith;
	}

	public String getVa5_pets() {
		return va5_pets;
	}

	public void setVa5_pets(String va5_pets) {
		this.va5_pets = va5_pets;
	}

	public String getVa5_rank() {
		return va5_rank;
	}

	public void setVa5_rank(String va5_rank) {
		this.va5_rank = va5_rank;
	}

	public String getVa5_rest() {
		return va5_rest;
	}

	public void setVa5_rest(String va5_rest) {
		this.va5_rest = va5_rest;
	}

	public String getVa5_skills() {
		return va5_skills;
	}

	public void setVa5_skills(String va5_skills) {
		this.va5_skills = va5_skills;
	}

	public String getVa5_zongjiao() {
		return va5_zongjiao;
	}

	public void setVa5_zongjiao(String va5_zongjiao) {
		this.va5_zongjiao = va5_zongjiao;
	}

	public String getVa5_makeroma() {
		return va5_makeroma;
	}

	public void setVa5_makeroma(String va5_makeroma) {
		this.va5_makeroma = va5_makeroma;
	}

	public String getVa4_comp_trade() {
		return va4_comp_trade;
	}

	public void setVa4_comp_trade(String va4_comp_trade) {
		this.va4_comp_trade = va4_comp_trade;
	}

	public String getVa4_comp_type() {
		return va4_comp_type;
	}

	public void setVa4_comp_type(String va4_comp_type) {
		this.va4_comp_type = va4_comp_type;
	}

	public String getVa4_grad_year() {
		return va4_grad_year;
	}

	public void setVa4_grad_year(String va4_grad_year) {
		this.va4_grad_year = va4_grad_year;
	}

	public String getVa4_income_desc() {
		return va4_income_desc;
	}

	public void setVa4_income_desc(String va4_income_desc) {
		this.va4_income_desc = va4_income_desc;
	}

	public String getVa4_job_state() {
		return va4_job_state;
	}

	public void setVa4_job_state(String va4_job_state) {
		this.va4_job_state = va4_job_state;
	}

	public String getVa4_lang() {
		return va4_lang;
	}

	public void setVa4_lang(String va4_lang) {
		this.va4_lang = va4_lang;
	}

	public String getVa4_prof_type() {
		return va4_prof_type;
	}

	public void setVa4_prof_type(String va4_prof_type) {
		this.va4_prof_type = va4_prof_type;
	}

	public String getJson_base_info() {
		return json_base_info;
	}

	public void setJson_base_info(String json_base_info) {
		this.json_base_info = json_base_info;
	}

	public String getJson_hobby_info() {
		return json_hobby_info;
	}

	public void setJson_hobby_info(String json_hobby_info) {
		this.json_hobby_info = json_hobby_info;
	}

	public String getJson_detailinfo() {
		return json_detailinfo;
	}

	public void setJson_detailinfo(String json_detailinfo) {
		this.json_detailinfo = json_detailinfo;
	}

	public String getJson_bodystyle() {
		return json_bodystyle;
	}

	public void setJson_bodystyle(String json_bodystyle) {
		this.json_bodystyle = json_bodystyle;
	}

	public String getJson_workinfo() {
		return json_workinfo;
	}

	public void setJson_workinfo(String json_workinfo) {
		this.json_workinfo = json_workinfo;
	}

	public String getJson_lifeinfo() {
		return json_lifeinfo;
	}

	public void setJson_lifeinfo(String json_lifeinfo) {
		this.json_lifeinfo = json_lifeinfo;
	}

	public String getVa_goopos() {
		return va_goopos;
	}

	public void setVa_goopos(String va_goopos) {
		this.va_goopos = va_goopos;
	}

	public String getVa_bs() {
		return va_bs;
	}

	public void setVa_bs(String va_bs) {
		this.va_bs = va_bs;
	}

	public String getVa_ec() {
		return va_ec;
	}

	public void setVa_ec(String va_ec) {
		this.va_ec = va_ec;
	}

	public String getVa_fs() {
		return va_fs;
	}

	public void setVa_fs(String va_fs) {
		this.va_fs = va_fs;
	}

	public String getVa_hc() {
		return va_hc;
	}

	public void setVa_hc(String va_hc) {
		this.va_hc = va_hc;
	}

	public String getVa_face_self() {
		return va_face_self;
	}

	public void setVa_face_self(String va_face_self) {
		this.va_face_self = va_face_self;
	}

	public String getVa_weight() {
		return va_weight;
	}

	public void setVa_weight(String va_weight) {
		this.va_weight = va_weight;
	}

	public String getVa_hs() {
		return va_hs;
	}

	public void setVa_hs(String va_hs) {
		this.va_hs = va_hs;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getXingzuo() {
		return xingzuo;
	}

	public void setXingzuo(String xingzuo) {
		this.xingzuo = xingzuo;
	}

	public String getShengxiao() {
		return shengxiao;
	}

	public void setShengxiao(String shengxiao) {
		this.shengxiao = shengxiao;
	}

	public String getBlood() {
		return blood;
	}

	public void setBlood(String blood) {
		this.blood = blood;
	}

	String overseas_experience = "";
	String profession = "";
	String zodiac = "";
	String family = "";
	String constellation = "";
	String live = "";
	String car = "";
	String salary = "";
	String assets_level = "";
	String mingzu = "";

	public String getMingzu() {
		return mingzu;
	}

	public void setMingzu(String mingzu) {
		this.mingzu = mingzu;
	}

	// ---外貌体型--
	String va3_weight = "";
	String va3_hair_style = "";
	String va3_face_style = "";
	String va3_hair_color = "";
	String va3_body_style = "";
	String va3_eye_color = "";
	String va3_attract_place = "";
	String va3_self_assert = "";
	// ------hobby info---

	String hobby_sports = "";
	String hobby_foods = "";
	String hobby_book = "";
	String hobby_music = "";
	String hobby_movie = "";
	String hobby_attention_item = "";
	String hobby_entainment = "";
	String hobby_amateur = "";
	String hobby_travel = "";

	public String getHobby_sports() {
		return hobby_sports;
	}

	public void setHobby_sports(String hobby_sports) {
		this.hobby_sports = hobby_sports;
	}

	public String getHobby_foods() {
		return hobby_foods;
	}

	public void setHobby_foods(String hobby_foods) {
		this.hobby_foods = hobby_foods;
	}

	public String getHobby_book() {
		return hobby_book;
	}

	public void setHobby_book(String hobby_book) {
		this.hobby_book = hobby_book;
	}

	public String getHobby_music() {
		return hobby_music;
	}

	public void setHobby_music(String hobby_music) {
		this.hobby_music = hobby_music;
	}

	public String getHobby_movie() {
		return hobby_movie;
	}

	public void setHobby_movie(String hobby_movie) {
		this.hobby_movie = hobby_movie;
	}

	public int getShield_count() {
		return shield_count;
	}

	public void setShield_count(int shield_count) {
		this.shield_count = shield_count;
	}

	public String getHobby_attention_item() {
		return hobby_attention_item;
	}

	public void setHobby_attention_item(String hobby_attention_item) {
		this.hobby_attention_item = hobby_attention_item;
	}

	public String getHobby_entainment() {
		return hobby_entainment;
	}

	public void setHobby_entainment(String hobby_entainment) {
		this.hobby_entainment = hobby_entainment;
	}

	public String getHobby_amateur() {
		return hobby_amateur;
	}

	public void setHobby_amateur(String hobby_amateur) {
		this.hobby_amateur = hobby_amateur;
	}

	public String getHobby_travel() {
		return hobby_travel;
	}

	public void setHobby_travel(String hobby_travel) {
		this.hobby_travel = hobby_travel;
	}

	public String getVa3_weight() {
		return va3_weight;
	}

	public void setVa3_weight(String va3_weight) {
		this.va3_weight = va3_weight;
	}

	public String getVa3_hair_style() {
		return va3_hair_style;
	}

	public void setVa3_hair_style(String va3_hair_style) {
		this.va3_hair_style = va3_hair_style;
	}

	public String getVa3_face_style() {
		return va3_face_style;
	}

	public void setVa3_face_style(String va3_face_style) {
		this.va3_face_style = va3_face_style;
	}

	public String getVa3_hair_color() {
		return va3_hair_color;
	}

	public void setVa3_hair_color(String va3_hair_color) {
		this.va3_hair_color = va3_hair_color;
	}

	public String getVa3_body_style() {
		return va3_body_style;
	}

	public void setVa3_body_style(String va3_body_style) {
		this.va3_body_style = va3_body_style;
	}

	public String getVa3_eye_color() {
		return va3_eye_color;
	}

	public void setVa3_eye_color(String va3_eye_color) {
		this.va3_eye_color = va3_eye_color;
	}

	public String getVa3_attract_place() {
		return va3_attract_place;
	}

	public void setVa3_attract_place(String va3_attract_place) {
		this.va3_attract_place = va3_attract_place;
	}

	public String getVa3_self_assert() {
		return va3_self_assert;
	}

	public void setVa3_self_assert(String va3_self_assert) {
		this.va3_self_assert = va3_self_assert;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getAge() {
		return age;
	}

	public void setAge(String age) {
		this.age = age;
	}

	public String getHeight() {
		return height;
	}

	public void setHeight(String height) {
		this.height = height;
	}

	public String getMarriage() {
		return marriage;
	}

	public void setMarriage(String marriage) {
		this.marriage = marriage;
	}

	public String getDegree() {
		return degree;
	}

	public void setDegree(String degree) {
		this.degree = degree;
	}

	public String getChild() {
		return child;
	}

	public void setChild(String child) {
		this.child = child;
	}

	public String getNationality() {
		return nationality;
	}

	public void setNationality(String nationality) {
		this.nationality = nationality;
	}

	public String getOverseas_experience() {
		return overseas_experience;
	}

	public void setOverseas_experience(String overseas_experience) {
		this.overseas_experience = overseas_experience;
	}

	public String getProfession() {
		return profession;
	}

	public void setProfession(String profession) {
		this.profession = profession;
	}

	public String getZodiac() {
		return zodiac;
	}

	public void setZodiac(String zodiac) {
		this.zodiac = zodiac;
	}

	public String getFamily() {
		return family;
	}

	public void setFamily(String family) {
		this.family = family;
	}

	public String getConstellation() {
		return constellation;
	}

	public void setConstellation(String constellation) {
		this.constellation = constellation;
	}

	public String getLive() {
		return live;
	}

	public void setLive(String live) {
		this.live = live;
	}

	public String getCar() {
		return car;
	}

	public void setCar(String car) {
		this.car = car;
	}

	public String getSalary() {
		return salary;
	}

	public void setSalary(String salary) {
		this.salary = salary;
	}

	public String getAssets_level() {
		return assets_level;
	}

	public void setAssets_level(String assets_level) {
		this.assets_level = assets_level;
	}

	public String getProvince_name() {
		return province_name;
	}

	public void setProvince_name(String province_name) {
		this.province_name = province_name;
	}

	public String getCity_name() {
		return city_name;
	}

	public void setCity_name(String city_name) {
		this.city_name = city_name;
	}

	public String getHeader() {
		return header;
	}

	public void setHeader(String header) {
		this.header = header;
	}

	public String getHeart_description() {
		return heart_description;
	}

	public void setHeart_description(String heart_description) {
		this.heart_description = heart_description;
	}
}
