package net.minecraft.client.resources;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.Lists;

import net.minecraft.client.resources.data.IMetadataSerializer;
import net.minecraft.util.ResourceLocation;

public class FallbackResourceManager implements IResourceManager {
	private static final Logger logger = LogManager.getLogger();
	protected final List<IResourcePack> resourcePacks = Lists.<IResourcePack>newArrayList();
	private final IMetadataSerializer frmMetadataSerializer;

	public FallbackResourceManager(IMetadataSerializer frmMetadataSerializerIn) {
		this.frmMetadataSerializer = frmMetadataSerializerIn;
	}

	public void addResourcePack(IResourcePack resourcePack) {
		this.resourcePacks.add(resourcePack);
	}

	public Set<String> getResourceDomains() {
		return null;
	}

	public IResource getResource(ResourceLocation location) throws IOException {
		IResourcePack iresourcepack = null;
		ResourceLocation resourcelocation = getLocationMcmeta(location);

		for (int i = this.resourcePacks.size() - 1; i >= 0; --i) {
			IResourcePack iresourcepack1 = (IResourcePack) this.resourcePacks.get(i);

			if (iresourcepack == null && iresourcepack1.resourceExists(resourcelocation)) {
				iresourcepack = iresourcepack1;
			}

			if (iresourcepack1.resourceExists(location)) {
				InputStream inputstream = null;

				if (iresourcepack != null) {
					inputstream = this.getInputStream(resourcelocation, iresourcepack);
				}

				return new SimpleResource(iresourcepack1.getPackName(), location, this.getInputStream(location, iresourcepack1), inputstream, this.frmMetadataSerializer);
			}
		}

		throw new FileNotFoundException(location.toString());
	}

	protected InputStream getInputStream(ResourceLocation location, IResourcePack resourcePack) throws IOException {
		InputStream inputstream = resourcePack.getInputStream(location);
		return (InputStream) (logger.isDebugEnabled() ? new FallbackResourceManager.InputStreamLeakedResourceLogger(inputstream, location, resourcePack.getPackName()) : inputstream);
	}

	public List<IResource> getAllResources(ResourceLocation location) throws IOException {
		List<IResource> list = Lists.<IResource>newArrayList();
		ResourceLocation resourcelocation = getLocationMcmeta(location);

		for (IResourcePack iresourcepack : this.resourcePacks) {
			if (iresourcepack.resourceExists(location)) {
				InputStream inputstream = iresourcepack.resourceExists(resourcelocation) ? this.getInputStream(resourcelocation, iresourcepack) : null;
				list.add(new SimpleResource(iresourcepack.getPackName(), location, this.getInputStream(location, iresourcepack), inputstream, this.frmMetadataSerializer));
			}
		}

		if (list.isEmpty()) {
			throw new FileNotFoundException(location.toString());
		} else {
			return list;
		}
	}

	static ResourceLocation getLocationMcmeta(ResourceLocation location) {
		return new ResourceLocation(location.getResourceDomain(), location.getResourcePath() + ".mcmeta");
	}

	static class InputStreamLeakedResourceLogger extends InputStream {
		private final InputStream field_177330_a;
		private final String field_177328_b;
		private boolean field_177329_c = false;

		public InputStreamLeakedResourceLogger(InputStream p_i46093_1_, ResourceLocation location, String p_i46093_3_) {
			this.field_177330_a = p_i46093_1_;
			ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
			(new Exception()).printStackTrace(new PrintStream(bytearrayoutputstream));
			this.field_177328_b = "Leaked resource: \'" + location + "\' loaded from pack: \'" + p_i46093_3_ + "\'\n" + bytearrayoutputstream.toString();
		}

		public void close() throws IOException {
			this.field_177330_a.close();
			this.field_177329_c = true;
		}

		protected void finalize() throws Throwable {
			if (!this.field_177329_c) {
				FallbackResourceManager.logger.warn(this.field_177328_b);
			}

			super.finalize();
		}

		public int read() throws IOException {
			return this.field_177330_a.read();
		}
	}
}
