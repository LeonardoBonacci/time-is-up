// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: the-unmoved-geo.proto

package guru.bonacci.timesup.model;

public final class TheUnmovedGeo {
  private TheUnmovedGeo() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  public interface ConnectDefault1OrBuilder extends
      // @@protoc_insertion_point(interface_extends:timesup.ConnectDefault1)
      com.google.protobuf.MessageOrBuilder {

    /**
     * <code>string ID = 1;</code>
     * @return The iD.
     */
    java.lang.String getID();
    /**
     * <code>string ID = 1;</code>
     * @return The bytes for iD.
     */
    com.google.protobuf.ByteString
        getIDBytes();

    /**
     * <code>double LATITUDE = 2;</code>
     * @return The lATITUDE.
     */
    double getLATITUDE();

    /**
     * <code>double LONGITUDE = 3;</code>
     * @return The lONGITUDE.
     */
    double getLONGITUDE();

    /**
     * <code>string GEOHASH = 4;</code>
     * @return The gEOHASH.
     */
    java.lang.String getGEOHASH();
    /**
     * <code>string GEOHASH = 4;</code>
     * @return The bytes for gEOHASH.
     */
    com.google.protobuf.ByteString
        getGEOHASHBytes();
  }
  /**
   * Protobuf type {@code timesup.ConnectDefault1}
   */
  public static final class ConnectDefault1 extends
      com.google.protobuf.GeneratedMessageV3 implements
      // @@protoc_insertion_point(message_implements:timesup.ConnectDefault1)
      ConnectDefault1OrBuilder {
  private static final long serialVersionUID = 0L;
    // Use ConnectDefault1.newBuilder() to construct.
    private ConnectDefault1(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
      super(builder);
    }
    private ConnectDefault1() {
      iD_ = "";
      gEOHASH_ = "";
    }

    @java.lang.Override
    @SuppressWarnings({"unused"})
    protected java.lang.Object newInstance(
        UnusedPrivateParameter unused) {
      return new ConnectDefault1();
    }

    @java.lang.Override
    public final com.google.protobuf.UnknownFieldSet
    getUnknownFields() {
      return this.unknownFields;
    }
    private ConnectDefault1(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      this();
      if (extensionRegistry == null) {
        throw new java.lang.NullPointerException();
      }
      com.google.protobuf.UnknownFieldSet.Builder unknownFields =
          com.google.protobuf.UnknownFieldSet.newBuilder();
      try {
        boolean done = false;
        while (!done) {
          int tag = input.readTag();
          switch (tag) {
            case 0:
              done = true;
              break;
            case 10: {
              java.lang.String s = input.readStringRequireUtf8();

              iD_ = s;
              break;
            }
            case 17: {

              lATITUDE_ = input.readDouble();
              break;
            }
            case 25: {

              lONGITUDE_ = input.readDouble();
              break;
            }
            case 34: {
              java.lang.String s = input.readStringRequireUtf8();

              gEOHASH_ = s;
              break;
            }
            default: {
              if (!parseUnknownField(
                  input, unknownFields, extensionRegistry, tag)) {
                done = true;
              }
              break;
            }
          }
        }
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        throw e.setUnfinishedMessage(this);
      } catch (java.io.IOException e) {
        throw new com.google.protobuf.InvalidProtocolBufferException(
            e).setUnfinishedMessage(this);
      } finally {
        this.unknownFields = unknownFields.build();
        makeExtensionsImmutable();
      }
    }
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return guru.bonacci.timesup.model.TheUnmovedGeo.internal_static_timesup_ConnectDefault1_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return guru.bonacci.timesup.model.TheUnmovedGeo.internal_static_timesup_ConnectDefault1_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              guru.bonacci.timesup.model.TheUnmovedGeo.ConnectDefault1.class, guru.bonacci.timesup.model.TheUnmovedGeo.ConnectDefault1.Builder.class);
    }

    public static final int ID_FIELD_NUMBER = 1;
    private volatile java.lang.Object iD_;
    /**
     * <code>string ID = 1;</code>
     * @return The iD.
     */
    @java.lang.Override
    public java.lang.String getID() {
      java.lang.Object ref = iD_;
      if (ref instanceof java.lang.String) {
        return (java.lang.String) ref;
      } else {
        com.google.protobuf.ByteString bs = 
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        iD_ = s;
        return s;
      }
    }
    /**
     * <code>string ID = 1;</code>
     * @return The bytes for iD.
     */
    @java.lang.Override
    public com.google.protobuf.ByteString
        getIDBytes() {
      java.lang.Object ref = iD_;
      if (ref instanceof java.lang.String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        iD_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }

    public static final int LATITUDE_FIELD_NUMBER = 2;
    private double lATITUDE_;
    /**
     * <code>double LATITUDE = 2;</code>
     * @return The lATITUDE.
     */
    @java.lang.Override
    public double getLATITUDE() {
      return lATITUDE_;
    }

    public static final int LONGITUDE_FIELD_NUMBER = 3;
    private double lONGITUDE_;
    /**
     * <code>double LONGITUDE = 3;</code>
     * @return The lONGITUDE.
     */
    @java.lang.Override
    public double getLONGITUDE() {
      return lONGITUDE_;
    }

    public static final int GEOHASH_FIELD_NUMBER = 4;
    private volatile java.lang.Object gEOHASH_;
    /**
     * <code>string GEOHASH = 4;</code>
     * @return The gEOHASH.
     */
    @java.lang.Override
    public java.lang.String getGEOHASH() {
      java.lang.Object ref = gEOHASH_;
      if (ref instanceof java.lang.String) {
        return (java.lang.String) ref;
      } else {
        com.google.protobuf.ByteString bs = 
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        gEOHASH_ = s;
        return s;
      }
    }
    /**
     * <code>string GEOHASH = 4;</code>
     * @return The bytes for gEOHASH.
     */
    @java.lang.Override
    public com.google.protobuf.ByteString
        getGEOHASHBytes() {
      java.lang.Object ref = gEOHASH_;
      if (ref instanceof java.lang.String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        gEOHASH_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }

    private byte memoizedIsInitialized = -1;
    @java.lang.Override
    public final boolean isInitialized() {
      byte isInitialized = memoizedIsInitialized;
      if (isInitialized == 1) return true;
      if (isInitialized == 0) return false;

      memoizedIsInitialized = 1;
      return true;
    }

    @java.lang.Override
    public void writeTo(com.google.protobuf.CodedOutputStream output)
                        throws java.io.IOException {
      if (!getIDBytes().isEmpty()) {
        com.google.protobuf.GeneratedMessageV3.writeString(output, 1, iD_);
      }
      if (lATITUDE_ != 0D) {
        output.writeDouble(2, lATITUDE_);
      }
      if (lONGITUDE_ != 0D) {
        output.writeDouble(3, lONGITUDE_);
      }
      if (!getGEOHASHBytes().isEmpty()) {
        com.google.protobuf.GeneratedMessageV3.writeString(output, 4, gEOHASH_);
      }
      unknownFields.writeTo(output);
    }

    @java.lang.Override
    public int getSerializedSize() {
      int size = memoizedSize;
      if (size != -1) return size;

      size = 0;
      if (!getIDBytes().isEmpty()) {
        size += com.google.protobuf.GeneratedMessageV3.computeStringSize(1, iD_);
      }
      if (lATITUDE_ != 0D) {
        size += com.google.protobuf.CodedOutputStream
          .computeDoubleSize(2, lATITUDE_);
      }
      if (lONGITUDE_ != 0D) {
        size += com.google.protobuf.CodedOutputStream
          .computeDoubleSize(3, lONGITUDE_);
      }
      if (!getGEOHASHBytes().isEmpty()) {
        size += com.google.protobuf.GeneratedMessageV3.computeStringSize(4, gEOHASH_);
      }
      size += unknownFields.getSerializedSize();
      memoizedSize = size;
      return size;
    }

    @java.lang.Override
    public boolean equals(final java.lang.Object obj) {
      if (obj == this) {
       return true;
      }
      if (!(obj instanceof guru.bonacci.timesup.model.TheUnmovedGeo.ConnectDefault1)) {
        return super.equals(obj);
      }
      guru.bonacci.timesup.model.TheUnmovedGeo.ConnectDefault1 other = (guru.bonacci.timesup.model.TheUnmovedGeo.ConnectDefault1) obj;

      if (!getID()
          .equals(other.getID())) return false;
      if (java.lang.Double.doubleToLongBits(getLATITUDE())
          != java.lang.Double.doubleToLongBits(
              other.getLATITUDE())) return false;
      if (java.lang.Double.doubleToLongBits(getLONGITUDE())
          != java.lang.Double.doubleToLongBits(
              other.getLONGITUDE())) return false;
      if (!getGEOHASH()
          .equals(other.getGEOHASH())) return false;
      if (!unknownFields.equals(other.unknownFields)) return false;
      return true;
    }

    @java.lang.Override
    public int hashCode() {
      if (memoizedHashCode != 0) {
        return memoizedHashCode;
      }
      int hash = 41;
      hash = (19 * hash) + getDescriptor().hashCode();
      hash = (37 * hash) + ID_FIELD_NUMBER;
      hash = (53 * hash) + getID().hashCode();
      hash = (37 * hash) + LATITUDE_FIELD_NUMBER;
      hash = (53 * hash) + com.google.protobuf.Internal.hashLong(
          java.lang.Double.doubleToLongBits(getLATITUDE()));
      hash = (37 * hash) + LONGITUDE_FIELD_NUMBER;
      hash = (53 * hash) + com.google.protobuf.Internal.hashLong(
          java.lang.Double.doubleToLongBits(getLONGITUDE()));
      hash = (37 * hash) + GEOHASH_FIELD_NUMBER;
      hash = (53 * hash) + getGEOHASH().hashCode();
      hash = (29 * hash) + unknownFields.hashCode();
      memoizedHashCode = hash;
      return hash;
    }

    public static guru.bonacci.timesup.model.TheUnmovedGeo.ConnectDefault1 parseFrom(
        java.nio.ByteBuffer data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static guru.bonacci.timesup.model.TheUnmovedGeo.ConnectDefault1 parseFrom(
        java.nio.ByteBuffer data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static guru.bonacci.timesup.model.TheUnmovedGeo.ConnectDefault1 parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static guru.bonacci.timesup.model.TheUnmovedGeo.ConnectDefault1 parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static guru.bonacci.timesup.model.TheUnmovedGeo.ConnectDefault1 parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static guru.bonacci.timesup.model.TheUnmovedGeo.ConnectDefault1 parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static guru.bonacci.timesup.model.TheUnmovedGeo.ConnectDefault1 parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static guru.bonacci.timesup.model.TheUnmovedGeo.ConnectDefault1 parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }
    public static guru.bonacci.timesup.model.TheUnmovedGeo.ConnectDefault1 parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input);
    }
    public static guru.bonacci.timesup.model.TheUnmovedGeo.ConnectDefault1 parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    public static guru.bonacci.timesup.model.TheUnmovedGeo.ConnectDefault1 parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static guru.bonacci.timesup.model.TheUnmovedGeo.ConnectDefault1 parseFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }

    @java.lang.Override
    public Builder newBuilderForType() { return newBuilder(); }
    public static Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
    }
    public static Builder newBuilder(guru.bonacci.timesup.model.TheUnmovedGeo.ConnectDefault1 prototype) {
      return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
    }
    @java.lang.Override
    public Builder toBuilder() {
      return this == DEFAULT_INSTANCE
          ? new Builder() : new Builder().mergeFrom(this);
    }

    @java.lang.Override
    protected Builder newBuilderForType(
        com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
      Builder builder = new Builder(parent);
      return builder;
    }
    /**
     * Protobuf type {@code timesup.ConnectDefault1}
     */
    public static final class Builder extends
        com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
        // @@protoc_insertion_point(builder_implements:timesup.ConnectDefault1)
        guru.bonacci.timesup.model.TheUnmovedGeo.ConnectDefault1OrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return guru.bonacci.timesup.model.TheUnmovedGeo.internal_static_timesup_ConnectDefault1_descriptor;
      }

      @java.lang.Override
      protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
          internalGetFieldAccessorTable() {
        return guru.bonacci.timesup.model.TheUnmovedGeo.internal_static_timesup_ConnectDefault1_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                guru.bonacci.timesup.model.TheUnmovedGeo.ConnectDefault1.class, guru.bonacci.timesup.model.TheUnmovedGeo.ConnectDefault1.Builder.class);
      }

      // Construct using guru.bonacci.timesup.model.TheUnmovedGeo.ConnectDefault1.newBuilder()
      private Builder() {
        maybeForceBuilderInitialization();
      }

      private Builder(
          com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
        super(parent);
        maybeForceBuilderInitialization();
      }
      private void maybeForceBuilderInitialization() {
        if (com.google.protobuf.GeneratedMessageV3
                .alwaysUseFieldBuilders) {
        }
      }
      @java.lang.Override
      public Builder clear() {
        super.clear();
        iD_ = "";

        lATITUDE_ = 0D;

        lONGITUDE_ = 0D;

        gEOHASH_ = "";

        return this;
      }

      @java.lang.Override
      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return guru.bonacci.timesup.model.TheUnmovedGeo.internal_static_timesup_ConnectDefault1_descriptor;
      }

      @java.lang.Override
      public guru.bonacci.timesup.model.TheUnmovedGeo.ConnectDefault1 getDefaultInstanceForType() {
        return guru.bonacci.timesup.model.TheUnmovedGeo.ConnectDefault1.getDefaultInstance();
      }

      @java.lang.Override
      public guru.bonacci.timesup.model.TheUnmovedGeo.ConnectDefault1 build() {
        guru.bonacci.timesup.model.TheUnmovedGeo.ConnectDefault1 result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      @java.lang.Override
      public guru.bonacci.timesup.model.TheUnmovedGeo.ConnectDefault1 buildPartial() {
        guru.bonacci.timesup.model.TheUnmovedGeo.ConnectDefault1 result = new guru.bonacci.timesup.model.TheUnmovedGeo.ConnectDefault1(this);
        result.iD_ = iD_;
        result.lATITUDE_ = lATITUDE_;
        result.lONGITUDE_ = lONGITUDE_;
        result.gEOHASH_ = gEOHASH_;
        onBuilt();
        return result;
      }

      @java.lang.Override
      public Builder clone() {
        return super.clone();
      }
      @java.lang.Override
      public Builder setField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          java.lang.Object value) {
        return super.setField(field, value);
      }
      @java.lang.Override
      public Builder clearField(
          com.google.protobuf.Descriptors.FieldDescriptor field) {
        return super.clearField(field);
      }
      @java.lang.Override
      public Builder clearOneof(
          com.google.protobuf.Descriptors.OneofDescriptor oneof) {
        return super.clearOneof(oneof);
      }
      @java.lang.Override
      public Builder setRepeatedField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          int index, java.lang.Object value) {
        return super.setRepeatedField(field, index, value);
      }
      @java.lang.Override
      public Builder addRepeatedField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          java.lang.Object value) {
        return super.addRepeatedField(field, value);
      }
      @java.lang.Override
      public Builder mergeFrom(com.google.protobuf.Message other) {
        if (other instanceof guru.bonacci.timesup.model.TheUnmovedGeo.ConnectDefault1) {
          return mergeFrom((guru.bonacci.timesup.model.TheUnmovedGeo.ConnectDefault1)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(guru.bonacci.timesup.model.TheUnmovedGeo.ConnectDefault1 other) {
        if (other == guru.bonacci.timesup.model.TheUnmovedGeo.ConnectDefault1.getDefaultInstance()) return this;
        if (!other.getID().isEmpty()) {
          iD_ = other.iD_;
          onChanged();
        }
        if (other.getLATITUDE() != 0D) {
          setLATITUDE(other.getLATITUDE());
        }
        if (other.getLONGITUDE() != 0D) {
          setLONGITUDE(other.getLONGITUDE());
        }
        if (!other.getGEOHASH().isEmpty()) {
          gEOHASH_ = other.gEOHASH_;
          onChanged();
        }
        this.mergeUnknownFields(other.unknownFields);
        onChanged();
        return this;
      }

      @java.lang.Override
      public final boolean isInitialized() {
        return true;
      }

      @java.lang.Override
      public Builder mergeFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws java.io.IOException {
        guru.bonacci.timesup.model.TheUnmovedGeo.ConnectDefault1 parsedMessage = null;
        try {
          parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          parsedMessage = (guru.bonacci.timesup.model.TheUnmovedGeo.ConnectDefault1) e.getUnfinishedMessage();
          throw e.unwrapIOException();
        } finally {
          if (parsedMessage != null) {
            mergeFrom(parsedMessage);
          }
        }
        return this;
      }

      private java.lang.Object iD_ = "";
      /**
       * <code>string ID = 1;</code>
       * @return The iD.
       */
      public java.lang.String getID() {
        java.lang.Object ref = iD_;
        if (!(ref instanceof java.lang.String)) {
          com.google.protobuf.ByteString bs =
              (com.google.protobuf.ByteString) ref;
          java.lang.String s = bs.toStringUtf8();
          iD_ = s;
          return s;
        } else {
          return (java.lang.String) ref;
        }
      }
      /**
       * <code>string ID = 1;</code>
       * @return The bytes for iD.
       */
      public com.google.protobuf.ByteString
          getIDBytes() {
        java.lang.Object ref = iD_;
        if (ref instanceof String) {
          com.google.protobuf.ByteString b = 
              com.google.protobuf.ByteString.copyFromUtf8(
                  (java.lang.String) ref);
          iD_ = b;
          return b;
        } else {
          return (com.google.protobuf.ByteString) ref;
        }
      }
      /**
       * <code>string ID = 1;</code>
       * @param value The iD to set.
       * @return This builder for chaining.
       */
      public Builder setID(
          java.lang.String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  
        iD_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>string ID = 1;</code>
       * @return This builder for chaining.
       */
      public Builder clearID() {
        
        iD_ = getDefaultInstance().getID();
        onChanged();
        return this;
      }
      /**
       * <code>string ID = 1;</code>
       * @param value The bytes for iD to set.
       * @return This builder for chaining.
       */
      public Builder setIDBytes(
          com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
        
        iD_ = value;
        onChanged();
        return this;
      }

      private double lATITUDE_ ;
      /**
       * <code>double LATITUDE = 2;</code>
       * @return The lATITUDE.
       */
      @java.lang.Override
      public double getLATITUDE() {
        return lATITUDE_;
      }
      /**
       * <code>double LATITUDE = 2;</code>
       * @param value The lATITUDE to set.
       * @return This builder for chaining.
       */
      public Builder setLATITUDE(double value) {
        
        lATITUDE_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>double LATITUDE = 2;</code>
       * @return This builder for chaining.
       */
      public Builder clearLATITUDE() {
        
        lATITUDE_ = 0D;
        onChanged();
        return this;
      }

      private double lONGITUDE_ ;
      /**
       * <code>double LONGITUDE = 3;</code>
       * @return The lONGITUDE.
       */
      @java.lang.Override
      public double getLONGITUDE() {
        return lONGITUDE_;
      }
      /**
       * <code>double LONGITUDE = 3;</code>
       * @param value The lONGITUDE to set.
       * @return This builder for chaining.
       */
      public Builder setLONGITUDE(double value) {
        
        lONGITUDE_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>double LONGITUDE = 3;</code>
       * @return This builder for chaining.
       */
      public Builder clearLONGITUDE() {
        
        lONGITUDE_ = 0D;
        onChanged();
        return this;
      }

      private java.lang.Object gEOHASH_ = "";
      /**
       * <code>string GEOHASH = 4;</code>
       * @return The gEOHASH.
       */
      public java.lang.String getGEOHASH() {
        java.lang.Object ref = gEOHASH_;
        if (!(ref instanceof java.lang.String)) {
          com.google.protobuf.ByteString bs =
              (com.google.protobuf.ByteString) ref;
          java.lang.String s = bs.toStringUtf8();
          gEOHASH_ = s;
          return s;
        } else {
          return (java.lang.String) ref;
        }
      }
      /**
       * <code>string GEOHASH = 4;</code>
       * @return The bytes for gEOHASH.
       */
      public com.google.protobuf.ByteString
          getGEOHASHBytes() {
        java.lang.Object ref = gEOHASH_;
        if (ref instanceof String) {
          com.google.protobuf.ByteString b = 
              com.google.protobuf.ByteString.copyFromUtf8(
                  (java.lang.String) ref);
          gEOHASH_ = b;
          return b;
        } else {
          return (com.google.protobuf.ByteString) ref;
        }
      }
      /**
       * <code>string GEOHASH = 4;</code>
       * @param value The gEOHASH to set.
       * @return This builder for chaining.
       */
      public Builder setGEOHASH(
          java.lang.String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  
        gEOHASH_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>string GEOHASH = 4;</code>
       * @return This builder for chaining.
       */
      public Builder clearGEOHASH() {
        
        gEOHASH_ = getDefaultInstance().getGEOHASH();
        onChanged();
        return this;
      }
      /**
       * <code>string GEOHASH = 4;</code>
       * @param value The bytes for gEOHASH to set.
       * @return This builder for chaining.
       */
      public Builder setGEOHASHBytes(
          com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
        
        gEOHASH_ = value;
        onChanged();
        return this;
      }
      @java.lang.Override
      public final Builder setUnknownFields(
          final com.google.protobuf.UnknownFieldSet unknownFields) {
        return super.setUnknownFields(unknownFields);
      }

      @java.lang.Override
      public final Builder mergeUnknownFields(
          final com.google.protobuf.UnknownFieldSet unknownFields) {
        return super.mergeUnknownFields(unknownFields);
      }


      // @@protoc_insertion_point(builder_scope:timesup.ConnectDefault1)
    }

    // @@protoc_insertion_point(class_scope:timesup.ConnectDefault1)
    private static final guru.bonacci.timesup.model.TheUnmovedGeo.ConnectDefault1 DEFAULT_INSTANCE;
    static {
      DEFAULT_INSTANCE = new guru.bonacci.timesup.model.TheUnmovedGeo.ConnectDefault1();
    }

    public static guru.bonacci.timesup.model.TheUnmovedGeo.ConnectDefault1 getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }

    private static final com.google.protobuf.Parser<ConnectDefault1>
        PARSER = new com.google.protobuf.AbstractParser<ConnectDefault1>() {
      @java.lang.Override
      public ConnectDefault1 parsePartialFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
        return new ConnectDefault1(input, extensionRegistry);
      }
    };

    public static com.google.protobuf.Parser<ConnectDefault1> parser() {
      return PARSER;
    }

    @java.lang.Override
    public com.google.protobuf.Parser<ConnectDefault1> getParserForType() {
      return PARSER;
    }

    @java.lang.Override
    public guru.bonacci.timesup.model.TheUnmovedGeo.ConnectDefault1 getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }

  }

  private static final com.google.protobuf.Descriptors.Descriptor
    internal_static_timesup_ConnectDefault1_descriptor;
  private static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_timesup_ConnectDefault1_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\025the-unmoved-geo.proto\022\007timesup\"S\n\017Conn" +
      "ectDefault1\022\n\n\002ID\030\001 \001(\t\022\020\n\010LATITUDE\030\002 \001(" +
      "\001\022\021\n\tLONGITUDE\030\003 \001(\001\022\017\n\007GEOHASH\030\004 \001(\tB+\n" +
      "\032guru.bonacci.timesup.modelB\rTheUnmovedG" +
      "eob\006proto3"
    };
    descriptor = com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
        });
    internal_static_timesup_ConnectDefault1_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_timesup_ConnectDefault1_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_timesup_ConnectDefault1_descriptor,
        new java.lang.String[] { "ID", "LATITUDE", "LONGITUDE", "GEOHASH", });
  }

  // @@protoc_insertion_point(outer_class_scope)
}