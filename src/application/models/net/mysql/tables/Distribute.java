package application.models.net.mysql.tables;

public class Distribute {
	private int id_command = 1;
	private int id_type_table = 1;
	private int unique_item = 0;
	private int id_scales = 0;
	private int id_condition = 0;
	private int id_templates = 0;
	private int id_barcodes = 0;
	private float price = 0.0F;
	private String description = "";

	public Distribute() {
		// TODO Auto-generated constructor stub
	}

	public int getId_command() {
		return id_command;
	}

	public void setId_command(int id_command) {
		this.id_command = id_command;
	}

	public int getId_type_table() {
		return id_type_table;
	}

	public void setId_type_table(int id_type_table) {
		this.id_type_table = id_type_table;
	}

	public int getUnique_item() {
		return unique_item;
	}

	public void setUnique_item(int unique_item) {
		this.unique_item = unique_item;
	}

	public int getId_scales() {
		return id_scales;
	}

	public void setId_scales(int id_scales) {
		this.id_scales = id_scales;
	}

	public int getId_condition() {
		return id_condition;
	}

	public void setId_condition(int id_condition) {
		this.id_condition = id_condition;
	}

	public int getId_templates() {
		return id_templates;
	}

	public void setId_templates(int id_templates) {
		this.id_templates = id_templates;
	}

	public int getId_barcodes() {
		return id_barcodes;
	}

	public void setId_barcodes(int id_barcodes) {
		this.id_barcodes = id_barcodes;
	}

	public float getPrice() {
		return price;
	}

	public void setPrice(float price) {
		this.price = price;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
