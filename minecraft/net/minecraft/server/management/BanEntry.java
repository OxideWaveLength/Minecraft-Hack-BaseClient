package net.minecraft.server.management;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.gson.JsonObject;

public abstract class BanEntry<T> extends UserListEntry<T> {
	public static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
	protected final Date banStartDate;
	protected final String bannedBy;
	protected final Date banEndDate;
	protected final String reason;

	public BanEntry(T valueIn, Date startDate, String banner, Date endDate, String banReason) {
		super(valueIn);
		this.banStartDate = startDate == null ? new Date() : startDate;
		this.bannedBy = banner == null ? "(Unknown)" : banner;
		this.banEndDate = endDate;
		this.reason = banReason == null ? "Banned by an operator." : banReason;
	}

	protected BanEntry(T p_i1174_1_, JsonObject p_i1174_2_) {
		super(p_i1174_1_, p_i1174_2_);
		Date date;

		try {
			date = p_i1174_2_.has("created") ? dateFormat.parse(p_i1174_2_.get("created").getAsString()) : new Date();
		} catch (ParseException var7) {
			date = new Date();
		}

		this.banStartDate = date;
		this.bannedBy = p_i1174_2_.has("source") ? p_i1174_2_.get("source").getAsString() : "(Unknown)";
		Date date1;

		try {
			date1 = p_i1174_2_.has("expires") ? dateFormat.parse(p_i1174_2_.get("expires").getAsString()) : null;
		} catch (ParseException var6) {
			date1 = null;
		}

		this.banEndDate = date1;
		this.reason = p_i1174_2_.has("reason") ? p_i1174_2_.get("reason").getAsString() : "Banned by an operator.";
	}

	public Date getBanEndDate() {
		return this.banEndDate;
	}

	public String getBanReason() {
		return this.reason;
	}

	boolean hasBanExpired() {
		return this.banEndDate == null ? false : this.banEndDate.before(new Date());
	}

	protected void onSerialization(JsonObject data) {
		data.addProperty("created", dateFormat.format(this.banStartDate));
		data.addProperty("source", this.bannedBy);
		data.addProperty("expires", this.banEndDate == null ? "forever" : dateFormat.format(this.banEndDate));
		data.addProperty("reason", this.reason);
	}
}
