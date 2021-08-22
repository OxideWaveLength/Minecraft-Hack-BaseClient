package ibxm;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;

/**
 * A streaming mod/xm play back system
 * 
 * @author Kevin Glass
 */
public class OpenALMODPlayer {
	/** The size of the sections to stream from the mod file */
	private static final int sectionSize = 4096 * 10;
	
	/** Holds the OpenAL buffer names */
	private IntBuffer bufferNames;
	/** The IBXM reference */
	private IBXM ibxm;
	/** The length of the track in frames */
	private int songDuration;
	/** The data read for this section */
	private byte[] data = new byte[sectionSize * 4];
	/** The byte buffer passed to OpenAL containing the section */
	private ByteBuffer bufferData = BufferUtils.createByteBuffer(sectionSize * 4);
	/** The buffer holding the names of the OpenAL buffer thats been fully played back */
	private IntBuffer unqueued = BufferUtils.createIntBuffer(1);
	/** The source we're playing back on */
    private int source;
    /** True if sound works */
	private boolean soundWorks = true;
	/** The module being played */
	private Module module;
	/** True if we should loop the track */
	private boolean loop;
	/** True if we've completed play back */
	private boolean done = true;
	/** The number of buffers remaining to be played back */
	private int remainingBufferCount;
	
	/**
	 * Initialise OpenAL LWJGL styley
	 */
    public void init() {
    	try {
			AL.create();
			soundWorks = true;
		} catch (LWJGLException e) {
			System.err.println("Failed to initialise LWJGL OpenAL");
			soundWorks = false;
			return;
		}
		
		if (soundWorks) {
			IntBuffer sources = BufferUtils.createIntBuffer(1);
			AL10.alGenSources(sources);
			
			if (AL10.alGetError() != AL10.AL_NO_ERROR) {
				System.err.println("Failed to create sources");
				soundWorks = false;
			} else {
				source = sources.get(0);
			}
			
		}
    }
    
    /**
     * Play a mod or xm track streamed from the specified location
     * 
     * @param in The input stream to read the music from
     * @param loop True if the track should be looped
     * @param start True if the music should be started
     * @throws IOException The input stream to read the music from
     */
    public void play(InputStream in, boolean loop, boolean start) throws IOException {
    	play(source, in, loop, start);
    }

    /**
     * Play a mod or xm track streamed from the specified location
     * 
     * @param source The OpenAL source to play the music on
     * @param in The input stream to read the music from
     * @param loop True if the track should be looped
     * @param start True if the music should be started
     * @throws IOException The input stream to read the music from
     */
	public void play(int source, InputStream in, boolean loop, boolean start) throws IOException {
		if (!soundWorks) {
			return;
		}

		done = false;
    	this.loop = loop;
		this.source = source;
		
		module = loadModule(in);
		play(module, source, loop, start);
	}
	
	/**
     * Play a mod or xm track streamed from the specified location
     * 
     * @param module The moudle to play back
     * @param source The OpenAL source to play the music on
     * @param start True if the music should be started
     * @param loop True if the track should be looped
     */
	public void play(Module module, int source, boolean loop, boolean start) {
		this.source = source;
		this.loop = loop;
		this.module = module;
		done = false;
		
		ibxm = new IBXM(48000);
		ibxm.set_module(module);
		songDuration = ibxm.calculate_song_duration();

		if (bufferNames != null) {
			AL10.alSourceStop(source);
			bufferNames.flip();
			AL10.alDeleteBuffers(bufferNames);
		}
		
		bufferNames = BufferUtils.createIntBuffer(2);
		AL10.alGenBuffers(bufferNames);
		remainingBufferCount = 2;
		
		for (int i=0;i<2;i++) {
	        stream(bufferNames.get(i));
		}
        AL10.alSourceQueueBuffers(source, bufferNames);
		AL10.alSourcef(source, AL10.AL_PITCH, 1.0f);
		AL10.alSourcef(source, AL10.AL_GAIN, 1.0f); 
		
		if (start) {
			AL10.alSourcePlay(source);
		}
	}
	
	/**
	 * Setup the playback properties
	 * 
	 * @param pitch The pitch to play back at
	 * @param gain The volume to play back at
	 */
	public void setup(float pitch, float gain) {
		AL10.alSourcef(source, AL10.AL_PITCH, pitch);
		AL10.alSourcef(source, AL10.AL_GAIN, gain); 
	}
	
	/**
	 * Check if the playback is complete. Note this will never
	 * return true if we're looping
	 * 
	 * @return True if we're looping
	 */
	public boolean done() {
		return done;
	}
	
	/**
	 * Load a module using the IBXM
	 * 
	 * @param in The input stream to read the module from
	 * @return The module loaded
	 * @throws IOException Indicates a failure to access the module
	 */
	public static Module loadModule(InputStream in) throws IOException {
		Module module;
		DataInputStream din;
		byte[] xm_header, s3m_header, mod_header, output_buffer;
		int frames;
		
		din = new DataInputStream(in);
		module = null;
		xm_header = new byte[ 60 ];
		din.readFully( xm_header );
		
		if( FastTracker2.is_xm( xm_header ) ) {
			module = FastTracker2.load_xm( xm_header, din );
		} else {
			s3m_header = new byte[ 96 ];
			System.arraycopy( xm_header, 0, s3m_header, 0, 60 );
			din.readFully( s3m_header, 60, 36 );
			
			if( ScreamTracker3.is_s3m( s3m_header ) ) {
				module = ScreamTracker3.load_s3m( s3m_header, din );
			} else {
				mod_header = new byte[ 1084 ];
				System.arraycopy( s3m_header, 0, mod_header, 0, 96 );
				din.readFully( mod_header, 96, 988 );
				module = ProTracker.load_mod( mod_header, din );
			}
		}
		din.close();
		
		return module;
	}
	
	/**
	 * Poll the bufferNames - check if we need to fill the bufferNames with another
	 * section. 
	 * 
	 * Most of the time this should be reasonably quick
	 */
	public void update() {
		if (done) {
			return;
		}
		
		int processed = AL10.alGetSourcei(source, AL10.AL_BUFFERS_PROCESSED);
		
		while (processed > 0) {
			unqueued.clear();
			
			AL10.alSourceUnqueueBuffers(source, unqueued);
	        if (stream(unqueued.get(0))) {
	        	AL10.alSourceQueueBuffers(source, unqueued);
	        } else {
	        	remainingBufferCount--;
	        	if (remainingBufferCount == 0) {
	        		done = true;
	        	}
	        }
	        processed--;
		}
		
		int state = AL10.alGetSourcei(source, AL10.AL_SOURCE_STATE);
	    
	    if (state != AL10.AL_PLAYING) {
	    	AL10.alSourcePlay(source);
	    }
	}
	
	/**
	 * Stream one section from the mod/xm into an OpenAL buffer
	 * 
	 * @param bufferId The ID of the buffer to fill
	 * @return True if another section was available
	 */
	public boolean stream(int bufferId) {
		int frames = sectionSize;
		boolean reset = false;
		boolean more = true;
		
		if (frames > songDuration) {
			frames = songDuration;
			reset = true;
		}
		
		ibxm.get_audio(data, frames);
		bufferData.clear();
		bufferData.put(data);
		bufferData.limit(frames * 4);
		
		if (reset) {
			if (loop) {
				ibxm.seek(0); 
				ibxm.set_module(module);
				songDuration = ibxm.calculate_song_duration();
			} else {
				more = false;
				songDuration -= frames;
			}
		} else {
			songDuration -= frames;
		}

		bufferData.flip();
		AL10.alBufferData(bufferId, AL10.AL_FORMAT_STEREO16, bufferData, 48000);
		
		return more;
	}
}

