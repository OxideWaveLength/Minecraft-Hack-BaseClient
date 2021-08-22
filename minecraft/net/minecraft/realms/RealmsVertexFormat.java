package net.minecraft.realms;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;

public class RealmsVertexFormat {
	private VertexFormat v;

	public RealmsVertexFormat(VertexFormat p_i46456_1_) {
		this.v = p_i46456_1_;
	}

	public RealmsVertexFormat from(VertexFormat p_from_1_) {
		this.v = p_from_1_;
		return this;
	}

	public VertexFormat getVertexFormat() {
		return this.v;
	}

	public void clear() {
		this.v.clear();
	}

	public int getUvOffset(int p_getUvOffset_1_) {
		return this.v.getUvOffsetById(p_getUvOffset_1_);
	}

	public int getElementCount() {
		return this.v.getElementCount();
	}

	public boolean hasColor() {
		return this.v.hasColor();
	}

	public boolean hasUv(int p_hasUv_1_) {
		return this.v.hasUvOffset(p_hasUv_1_);
	}

	public RealmsVertexFormatElement getElement(int p_getElement_1_) {
		return new RealmsVertexFormatElement(this.v.getElement(p_getElement_1_));
	}

	public RealmsVertexFormat addElement(RealmsVertexFormatElement p_addElement_1_) {
		return this.from(this.v.func_181721_a(p_addElement_1_.getVertexFormatElement()));
	}

	public int getColorOffset() {
		return this.v.getColorOffset();
	}

	public List<RealmsVertexFormatElement> getElements() {
		List<RealmsVertexFormatElement> list = new ArrayList();

		for (VertexFormatElement vertexformatelement : this.v.getElements()) {
			list.add(new RealmsVertexFormatElement(vertexformatelement));
		}

		return list;
	}

	public boolean hasNormal() {
		return this.v.hasNormal();
	}

	public int getVertexSize() {
		return this.v.getNextOffset();
	}

	public int getOffset(int p_getOffset_1_) {
		return this.v.func_181720_d(p_getOffset_1_);
	}

	public int getNormalOffset() {
		return this.v.getNormalOffset();
	}

	public int getIntegerSize() {
		return this.v.func_181719_f();
	}

	public boolean equals(Object p_equals_1_) {
		return this.v.equals(p_equals_1_);
	}

	public int hashCode() {
		return this.v.hashCode();
	}

	public String toString() {
		return this.v.toString();
	}
}
