package com.swater.meimeng.mutils.constant;

/**
 * @category 用户效相关常量
 */
public class MConstantUser {
	public static class UserAll_basic_info {

		public static String nickname = "nickname";
		public static String age = "age";
		public static String height = "height";
		public static String city = "city";
		public static String marriage = "marriage";
		public static String degree = "degree";
		public static String child = "child";

	}
	public static class UserAll_detail_info {
		
		public static String nationality = "nationality";
		public static String overseas_experience = "overseas_experience";
		public static String ethnic = "ethnic";
		public static String profession = "profession";
		public static String zodiac = "zodiac";
		public static String family = "family";
		public static String constellation = "constellation";
		public static String live = "live";
		public static String blood_type = "blood_type";
		public static String car = "car";
		public static String salary = "salary";
		public static String assets_level = "assets_level";
		
	}
	public static class UserAll_Appearance_info {
		
		public static String weight = "weight";
		public static String hairstyle = "hairstyle";
		public static String face_type = "face_type";
		public static String haircolor = "haircolor";
		public static String body_type = "body_type";
		public static String eye_color = "eye_color";
		public static String good_position = "good_position";
		public static String face_description = "face_description";
		
	}
	public static class UserAll_work_info {
		
		public static String company_trade = "company_trade";
		public static String company_type = "company_type";
		public static String work_status = "work_status";
		public static String income = "income";
		public static String graduation_time = "graduation_time";
		public static String profession = "profession";
		public static String language = "language";
		
	}
	public static class UserAll_life_info {
		
		public static String smoke = "smoke";
		public static String drinking = "drinking";
		public static String take_exercise = "take_exercise";
		public static String schedule = "schedule";
		public static String religion = "religion";
		public static String family_ranking = "family_ranking";
		public static String with_parents = "with_parents";
		public static String want_children = "want_children";
		public static String make_romantic = "make_romantic";
		public static String largest_consumer = "largest_consumer";
		
		public static String pet_value = "pet_value";
		public static String life_skill = "life_skill";
	}
	public static class UserAll_hobby_info {
		
		public static String like_sports = "like_sports";
		public static String like_food = "like_food";
		public static String like_book = "like_book";
		public static String like_music = "like_music";
		public static String like_movie = "like_movie";
		public static String attention_item = "attention_item";
		public static String entertainment = "entertainment";
		public static String amateur_like = "amateur_like";
		public static String travel = "travel";
		
	}
	public static class UserAll_personality_info {
		
		public static String type_id = "type_id";
		public static String type_value = "type_value";
		public static String question = "question";
		
	}

	public static class UserReg {

		public static String telephone = "telephone";
		public static String password = "password";
		public static String realname = "realname";
		public static String sex = "sex";// 1-男；2-女；
		public static String age = "age";
		public static String assets_level = "assets_level";
		public static String city_id = "city_id";

	}

	public static class UserProperty {
		// uid：用户ID，int；
		//
		// active：是否激活，int；1-未激活；2-激活；
		// vip_level，int；0-未付费;1…N - VIP1…VIPN;
		// nickname：昵称，string；

		public static String city_id = "city_id";
		public static String user_key = "key";
		public static String uid = "uid";
		public static String telephone = "telephone";
		public static String password = "password";
		/**
		 * @return active：是否激活，int；1-未激活；2-激活；
		 */
		public static String active = "active";
		/**
		 * @return sex：用户性别，int；1-男；2女；
		 */
		public static String sex = "sex";// 1-男；2-女；
		public static String age = "age";
		public static String nickname = "nickname";
		public static String vip_level = "vip_level";
		public static String content = "content";
		public static String blood_type = "blood_type";
		public static String ethnic_id = "ethnic_id";

		// -----------
		public static String weight = "weight";
		public static String hairstyle = "hairstyle";
		public static String face_type = "face_type";
		public static String haircolor = "haircolor";
		public static String body_type = "body_type";
		public static String eye_color = "eye_color";
		public static String good_position = "good_position";
		public static String face_description = "face_description";
		// ----------------------------

		public static String company_trade = "company_trade";
		public static String company_type = "company_type";
		public static String work_status = "work_status";
		public static String income = "income";
		public static String graduation_time = "graduation_time";
		public static String profession = "profession";
		public static String language = "language";

		// ---------------
		public static String smoke = "smoke";
		public static String drinking = "drinking";
		public static String take_exercise = "take_exercise";
		public static String schedule = "schedule";
		public static String religion = "religion";
		public static String family_ranking = "family_ranking";
		public static String with_parents = "with_parents";
		public static String want_children = "want_children";
		public static String make_romantic = "make_romantic";
		public static String largest_consumer = "largest_consumer";
		public static String pet_value = "pet_value";
		public static String life_skill = "life_skill";
		// ---------------------------------
		public static String like_sports = "like_sports";
		public static String like_food = "like_food";
		public static String like_book = "like_book";
		public static String like_music = "like_music";
		public static String like_movie = "like_movie";
		public static String like_attention_item = "attention_item";
		public static String like_entertainment = "entertainment";
		public static String like_amateur_like = "amateur_like";
		public static String like_travel = "travel";

		// -------------------
		public static String type_id = "type_id";
		public static String answer = "answer";
	}
}
