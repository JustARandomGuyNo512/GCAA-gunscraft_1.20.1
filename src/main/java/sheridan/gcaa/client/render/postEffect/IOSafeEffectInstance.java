package sheridan.gcaa.client.render.postEffect;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.shaders.*;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ChainedJsonException;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidClassException;
import java.io.Reader;
import java.util.List;
import java.util.Map;
import java.util.function.IntSupplier;

@OnlyIn(Dist.CLIENT)
public class IOSafeEffectInstance implements Effect, AutoCloseable {
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final AbstractUniform DUMMY_UNIFORM = new AbstractUniform();
    public static IOSafeEffectInstance lastAppliedEffect;
    public static int lastProgramId = -1;
    public final Map<String, IntSupplier> samplerMap = Maps.newHashMap();
    public final List<String> samplerNames = Lists.newArrayList();
    public final List<Integer> samplerLocations = Lists.newArrayList();
    public final List<Uniform> uniforms = Lists.newArrayList();
    public final List<Integer> uniformLocations = Lists.newArrayList();
    public final Map<String, Uniform> uniformMap = Maps.newHashMap();
    public final int programId;
    public final String name;
    public boolean dirty;
    public final BlendMode blend;
    public final List<Integer> attributes;
    public final List<String> attributeNames;
    public final EffectProgram vertexProgram;
    public final EffectProgram fragmentProgram;

    public IOSafeEffectInstance(ResourceManager pResourceManager, String pName) throws IOException {
        ResourceLocation rl = ResourceLocation.tryParse(pName);
        ResourceLocation resourcelocation = new ResourceLocation(rl.getNamespace(), "shaders/program/" + rl.getPath() + ".json");
        this.name = pName;
        Resource resource = pResourceManager.getResourceOrThrow(resourcelocation);

        try (Reader reader = resource.openAsReader()) {
            JsonObject jsonobject = GsonHelper.parse(reader);
            String s = GsonHelper.getAsString(jsonobject, "vertex");
            String s1 = GsonHelper.getAsString(jsonobject, "fragment");
            JsonArray jsonarray = GsonHelper.getAsJsonArray(jsonobject, "samplers", (JsonArray)null);
            if (jsonarray != null) {
                int i = 0;

                for(JsonElement jsonelement : jsonarray) {
                    try {
                        this.parseSamplerNode(jsonelement);
                    } catch (Exception exception2) {
                        ChainedJsonException chainedJsonException1 = ChainedJsonException.forException(exception2);
                        chainedJsonException1.prependJsonKey("samplers[" + i + "]");
                        throw chainedJsonException1;
                    }

                    ++i;
                }
            }

            JsonArray jsonarray1 = GsonHelper.getAsJsonArray(jsonobject, "attributes", (JsonArray)null);
            if (jsonarray1 != null) {
                int j = 0;
                this.attributes = Lists.newArrayListWithCapacity(jsonarray1.size());
                this.attributeNames = Lists.newArrayListWithCapacity(jsonarray1.size());

                for(JsonElement jsonelement1 : jsonarray1) {
                    try {
                        this.attributeNames.add(GsonHelper.convertToString(jsonelement1, "attribute"));
                    } catch (Exception exception1) {
                        ChainedJsonException chainedjsonexception2 = ChainedJsonException.forException(exception1);
                        chainedjsonexception2.prependJsonKey("attributes[" + j + "]");
                        throw chainedjsonexception2;
                    }
                    ++j;
                }
            } else {
                this.attributes = null;
                this.attributeNames = null;
            }

            JsonArray jsonarray2 = GsonHelper.getAsJsonArray(jsonobject, "uniforms", (JsonArray)null);
            if (jsonarray2 != null) {
                int k = 0;

                for(JsonElement jsonelement2 : jsonarray2) {
                    try {
                        this.parseUniformNode(jsonelement2);
                    } catch (Exception exception) {
                        ChainedJsonException chainedjsonexception3 = ChainedJsonException.forException(exception);
                        chainedjsonexception3.prependJsonKey("uniforms[" + k + "]");
                        throw chainedjsonexception3;
                    }
                    ++k;
                }
            }

            this.blend = parseBlendNode(GsonHelper.getAsJsonObject(jsonobject, "blend", (JsonObject)null));
            this.vertexProgram = getOrCreate(pResourceManager, Program.Type.VERTEX, s);
            this.fragmentProgram = getOrCreate(pResourceManager, Program.Type.FRAGMENT, s1);
            this.programId = ProgramManager.createProgram();
            ProgramManager.linkShader(this);
            this.updateLocations();
            if (this.attributeNames != null) {
                for(String s2 : this.attributeNames) {
                    int l = Uniform.glGetAttribLocation(this.programId, s2);
                    this.attributes.add(l);
                }
            }
        } catch (Exception exception3) {
            ChainedJsonException chainedjsonexception = ChainedJsonException.forException(exception3);
            chainedjsonexception.setFilenameAndFlush(resourcelocation.getPath() + " (" + resource.sourcePackId() + ")");
            throw chainedjsonexception;
        }

        this.markDirty();
    }

    public static EffectProgram getOrCreate(ResourceManager pResourceManager, Program.Type pProgramType, String pName) throws IOException {
        Program program = pProgramType.getPrograms().get(pName);
        if (program != null && !(program instanceof EffectProgram)) {
            throw new InvalidClassException("Program is not of type EffectProgram");
        } else {
            EffectProgram effectprogram;
            if (program == null) {
                ResourceLocation rl = ResourceLocation.tryParse(pName);
                ResourceLocation resourcelocation = new ResourceLocation(rl.getNamespace(), "shaders/program/" + rl.getPath() + pProgramType.getExtension());
                Resource resource = pResourceManager.getResourceOrThrow(resourcelocation);

                try (InputStream inputstream = resource.open()) {
                    effectprogram = EffectProgram.compileShader(pProgramType, pName, inputstream, resource.sourcePackId());
                }
            } else {
                effectprogram = (EffectProgram)program;
            }

            return effectprogram;
        }
    }

    public static BlendMode parseBlendNode(@Nullable JsonObject pJson) {
        if (pJson == null) {
            return new BlendMode();
        } else {
            int i = 32774;
            int j = 1;
            int k = 0;
            int l = 1;
            int i1 = 0;
            boolean flag = true;
            boolean flag1 = false;
            if (GsonHelper.isStringValue(pJson, "func")) {
                i = BlendMode.stringToBlendFunc(pJson.get("func").getAsString());
                if (i != 32774) {
                    flag = false;
                }
            }

            if (GsonHelper.isStringValue(pJson, "srcrgb")) {
                j = BlendMode.stringToBlendFactor(pJson.get("srcrgb").getAsString());
                if (j != 1) {
                    flag = false;
                }
            }

            if (GsonHelper.isStringValue(pJson, "dstrgb")) {
                k = BlendMode.stringToBlendFactor(pJson.get("dstrgb").getAsString());
                if (k != 0) {
                    flag = false;
                }
            }

            if (GsonHelper.isStringValue(pJson, "srcalpha")) {
                l = BlendMode.stringToBlendFactor(pJson.get("srcalpha").getAsString());
                if (l != 1) {
                    flag = false;
                }

                flag1 = true;
            }

            if (GsonHelper.isStringValue(pJson, "dstalpha")) {
                i1 = BlendMode.stringToBlendFactor(pJson.get("dstalpha").getAsString());
                if (i1 != 0) {
                    flag = false;
                }

                flag1 = true;
            }

            if (flag) {
                return new BlendMode();
            } else {
                return flag1 ? new BlendMode(j, k, l, i1, i) : new BlendMode(j, k, i);
            }
        }
    }

    public void close() {
        for(Uniform uniform : this.uniforms) {
            uniform.close();
        }

        ProgramManager.releaseProgram(this);
    }

    public void clear() {
        RenderSystem.assertOnRenderThread();
        ProgramManager.glUseProgram(0);
        lastProgramId = -1;
        lastAppliedEffect = null;

        for(int i = 0; i < this.samplerLocations.size(); ++i) {
            if (this.samplerMap.get(this.samplerNames.get(i)) != null) {
                GlStateManager._activeTexture('\u84c0' + i);
                GlStateManager._bindTexture(0);
            }
        }

    }

    public void apply() {
        RenderSystem.assertOnGameThread();
        this.dirty = false;
        lastAppliedEffect = this;
        this.blend.apply();
        if (this.programId != lastProgramId) {
            ProgramManager.glUseProgram(this.programId);
            lastProgramId = this.programId;
        }

        for(int i = 0; i < this.samplerLocations.size(); ++i) {
            String s = this.samplerNames.get(i);
            IntSupplier intsupplier = this.samplerMap.get(s);
            if (intsupplier != null) {
                RenderSystem.activeTexture('\u84c0' + i);
                int j = intsupplier.getAsInt();
                if (j != -1) {
                    RenderSystem.bindTexture(j);
                    Uniform.uploadInteger(this.samplerLocations.get(i), i);
                }
            }
        }

        for(Uniform uniform : this.uniforms) {
            uniform.upload();
        }

    }

    public void markDirty() {
        this.dirty = true;
    }

    @Nullable
    public Uniform getUniform(String pName) {
        RenderSystem.assertOnRenderThread();
        return this.uniformMap.get(pName);
    }

    public AbstractUniform safeGetUniform(String pName) {
        RenderSystem.assertOnGameThread();
        Uniform uniform = this.getUniform(pName);
        return uniform == null ? DUMMY_UNIFORM : uniform;
    }

    private void updateLocations() {
        RenderSystem.assertOnRenderThread();
        IntList intlist = new IntArrayList();

        for(int i = 0; i < this.samplerNames.size(); ++i) {
            String s = this.samplerNames.get(i);
            int j = Uniform.glGetUniformLocation(this.programId, s);
            if (j == -1) {
                LOGGER.warn("Shader {} could not find sampler named {} in the specified shader program.", this.name, s);
                this.samplerMap.remove(s);
                intlist.add(i);
            } else {
                this.samplerLocations.add(j);
            }
        }

        for(int l = intlist.size() - 1; l >= 0; --l) {
            this.samplerNames.remove(intlist.getInt(l));
        }

        for(Uniform uniform : this.uniforms) {
            String s1 = uniform.getName();
            int k = Uniform.glGetUniformLocation(this.programId, s1);
            if (k == -1) {
                LOGGER.warn("Shader {} could not find uniform named {} in the specified shader program.", this.name, s1);
            } else {
                this.uniformLocations.add(k);
                uniform.setLocation(k);
                this.uniformMap.put(s1, uniform);
            }
        }

    }

    private void parseSamplerNode(JsonElement pJson) {
        JsonObject jsonobject = GsonHelper.convertToJsonObject(pJson, "sampler");
        String s = GsonHelper.getAsString(jsonobject, "name");
        if (!GsonHelper.isStringValue(jsonobject, "file")) {
            this.samplerMap.put(s, null);
            this.samplerNames.add(s);
        } else {
            this.samplerNames.add(s);
        }
    }

    public void setSampler(String pName, IntSupplier pTextureId) {
        this.samplerMap.remove(pName);
        this.samplerMap.put(pName, pTextureId);
        this.markDirty();
    }

    private void parseUniformNode(JsonElement pJson) throws ChainedJsonException {
        JsonObject jsonobject = GsonHelper.convertToJsonObject(pJson, "uniform");
        String s = GsonHelper.getAsString(jsonobject, "name");
        int i = Uniform.getTypeFromString(GsonHelper.getAsString(jsonobject, "type"));
        int j = GsonHelper.getAsInt(jsonobject, "count");
        float[] afloat = new float[Math.max(j, 16)];
        JsonArray jsonarray = GsonHelper.getAsJsonArray(jsonobject, "values");
        if (jsonarray.size() != j && jsonarray.size() > 1) {
            throw new ChainedJsonException("Invalid amount of values specified (expected " + j + ", found " + jsonarray.size() + ")");
        } else {
            int k = 0;

            for(JsonElement jsonelement : jsonarray) {
                try {
                    afloat[k] = GsonHelper.convertToFloat(jsonelement, "value");
                } catch (Exception exception) {
                    ChainedJsonException chainedjsonexception = ChainedJsonException.forException(exception);
                    chainedjsonexception.prependJsonKey("values[" + k + "]");
                    throw chainedjsonexception;
                }

                ++k;
            }

            if (j > 1 && jsonarray.size() == 1) {
                while(k < j) {
                    afloat[k] = afloat[0];
                    ++k;
                }
            }

            int l = j > 1 && j <= 4 && i < 8 ? j - 1 : 0;
            Uniform uniform = new Uniform(s, i + l, j, this);
            if (i <= 3) {
                uniform.setSafe((int)afloat[0], (int)afloat[1], (int)afloat[2], (int)afloat[3]);
            } else if (i <= 7) {
                uniform.setSafe(afloat[0], afloat[1], afloat[2], afloat[3]);
            } else {
                uniform.set(afloat);
            }

            this.uniforms.add(uniform);
        }
    }

    public @NotNull Program getVertexProgram() {
        return this.vertexProgram;
    }

    public @NotNull Program getFragmentProgram() {
        return this.fragmentProgram;
    }

    public void attachToProgram() {
        this.fragmentProgram.attachToEffect(this);
        this.vertexProgram.attachToEffect(this);
    }

    public String getName() {
        return this.name;
    }

    public int getId() {
        return this.programId;
    }
}
