package application;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import application.models.net.database.mysql.tables.Codes;
import application.models.net.database.mysql.tables.Goods;
import application.models.net.database.mysql.tables.Scales;
import application.models.net.database.mysql.tables.Sections;
import application.models.net.database.mysql.tables.Templates;

public final class Converter {

	private Converter() throws IllegalAccessException {
		throw new IllegalAccessException("This constructor is private");
	}

	public static List<Goods> fromResultSetToGoodsList(ResultSet set) throws SQLException {
		List<Goods> goodsList = new ArrayList<>();

		while (set.next()) {
			Goods goods = new Goods();
			goods.setId(set.getInt("id"));
			goods.setNumber(set.getInt("code"));
			goods.setId_scales(set.getInt("id_scales"));
			goods.setId_sections(set.getInt("id_sections"));
			goods.setId_templates(set.getInt("id_templates"));
			goods.setId_barcodes(set.getInt("id_barcodes"));
			goods.setName(set.getString("name"));
			goods.setFull_name(set.getString("full_name"));
			goods.setPrice(set.getFloat("price"));
			goods.setType(set.getInt("type"));
			goods.setPre_code(set.getInt("pre_code"));
			goods.setBefore_validity(set.getInt("before_validity"));
			goods.setIngredients(set.getString("ingredients"));
			goods.setDataBlob(set.getBlob("data"));
			goods.setTara(set.getFloat("tara"));

			goodsList.add(goods);
		}

		return goodsList;
	}

	public static List<Codes> fromResultSetToCodesList(ResultSet set) throws SQLException {
		List<Codes> codesList = new ArrayList<>();

		while (set.next()) {
			codesList.add(new Codes(set));
		}

		return codesList;
	}

	public static List<Sections> fromResultSetToSectionsList(ResultSet set) throws SQLException {
		List<Sections> sectionsList = new ArrayList<>();

		while (set.next()) {
			sectionsList.add(new Sections(set, true));
		}

		return sectionsList;
	}

	public static List<Templates> fromResultSetToTemplatesList(ResultSet set) throws SQLException {
		List<Templates> templatesList = new ArrayList<>();

		while (set.next()) {
			templatesList.add(new Templates(set));
		}

		return templatesList;
	}

	public static List<Scales> fromResultSetToScalesList(ResultSet set) throws SQLException {
		List<Scales> scalesList = new ArrayList<>();
		while (set.next()) {
			scalesList.add(new Scales(set));
		}
		return scalesList;
	}
}
