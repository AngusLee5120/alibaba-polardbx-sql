/*
 * Copyright [2013-2021], Alibaba Group Holding Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: DumperServer.proto

package com.alibaba.polardbx.rpc.cdc;

/**
 * Protobuf type {@code dumper.BinaryLog}
 */
public final class BinaryLog extends
    com.google.protobuf.GeneratedMessageV3 implements
    // @@protoc_insertion_point(message_implements:dumper.BinaryLog)
    BinaryLogOrBuilder {
    private static final long serialVersionUID = 0L;

    // Use BinaryLog.newBuilder() to construct.
    private BinaryLog(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
        super(builder);
    }

    private BinaryLog() {
        logName_ = "";
    }

    @java.lang.Override
    @SuppressWarnings({"unused"})
    protected java.lang.Object newInstance(
        UnusedPrivateParameter unused) {
        return new BinaryLog();
    }

    @java.lang.Override
    public final com.google.protobuf.UnknownFieldSet
    getUnknownFields() {
        return this.unknownFields;
    }

    private BinaryLog(
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

                    logName_ = s;
                    break;
                }
                case 16: {

                    fileSize_ = input.readInt64();
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
        return DumperServer.internal_static_dumper_BinaryLog_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
    internalGetFieldAccessorTable() {
        return DumperServer.internal_static_dumper_BinaryLog_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                BinaryLog.class, BinaryLog.Builder.class);
    }

    public static final int LOGNAME_FIELD_NUMBER = 1;
    private volatile java.lang.Object logName_;

    /**
     * <code>string logName = 1;</code>
     *
     * @return The logName.
     */
    @java.lang.Override
    public java.lang.String getLogName() {
        java.lang.Object ref = logName_;
        if (ref instanceof java.lang.String) {
            return (java.lang.String) ref;
        } else {
            com.google.protobuf.ByteString bs =
                (com.google.protobuf.ByteString) ref;
            java.lang.String s = bs.toStringUtf8();
            logName_ = s;
            return s;
        }
    }

    /**
     * <code>string logName = 1;</code>
     *
     * @return The bytes for logName.
     */
    @java.lang.Override
    public com.google.protobuf.ByteString
    getLogNameBytes() {
        java.lang.Object ref = logName_;
        if (ref instanceof java.lang.String) {
            com.google.protobuf.ByteString b =
                com.google.protobuf.ByteString.copyFromUtf8(
                    (java.lang.String) ref);
            logName_ = b;
            return b;
        } else {
            return (com.google.protobuf.ByteString) ref;
        }
    }

    public static final int FILESIZE_FIELD_NUMBER = 2;
    private long fileSize_;

    /**
     * <code>int64 fileSize = 2;</code>
     *
     * @return The fileSize.
     */
    @java.lang.Override
    public long getFileSize() {
        return fileSize_;
    }

    private byte memoizedIsInitialized = -1;

    @java.lang.Override
    public final boolean isInitialized() {
        byte isInitialized = memoizedIsInitialized;
        if (isInitialized == 1) {
            return true;
        }
        if (isInitialized == 0) {
            return false;
        }

        memoizedIsInitialized = 1;
        return true;
    }

    @java.lang.Override
    public void writeTo(com.google.protobuf.CodedOutputStream output)
        throws java.io.IOException {
        if (!getLogNameBytes().isEmpty()) {
            com.google.protobuf.GeneratedMessageV3.writeString(output, 1, logName_);
        }
        if (fileSize_ != 0L) {
            output.writeInt64(2, fileSize_);
        }
        unknownFields.writeTo(output);
    }

    @java.lang.Override
    public int getSerializedSize() {
        int size = memoizedSize;
        if (size != -1) {
            return size;
        }

        size = 0;
        if (!getLogNameBytes().isEmpty()) {
            size += com.google.protobuf.GeneratedMessageV3.computeStringSize(1, logName_);
        }
        if (fileSize_ != 0L) {
            size += com.google.protobuf.CodedOutputStream
                .computeInt64Size(2, fileSize_);
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
        if (!(obj instanceof BinaryLog)) {
            return super.equals(obj);
        }
        BinaryLog other = (BinaryLog) obj;

        if (!getLogName()
            .equals(other.getLogName())) {
            return false;
        }
        if (getFileSize()
            != other.getFileSize()) {
            return false;
        }
        if (!unknownFields.equals(other.unknownFields)) {
            return false;
        }
        return true;
    }

    @java.lang.Override
    public int hashCode() {
        if (memoizedHashCode != 0) {
            return memoizedHashCode;
        }
        int hash = 41;
        hash = (19 * hash) + getDescriptor().hashCode();
        hash = (37 * hash) + LOGNAME_FIELD_NUMBER;
        hash = (53 * hash) + getLogName().hashCode();
        hash = (37 * hash) + FILESIZE_FIELD_NUMBER;
        hash = (53 * hash) + com.google.protobuf.Internal.hashLong(
            getFileSize());
        hash = (29 * hash) + unknownFields.hashCode();
        memoizedHashCode = hash;
        return hash;
    }

    public static BinaryLog parseFrom(
        java.nio.ByteBuffer data)
        throws com.google.protobuf.InvalidProtocolBufferException {
        return PARSER.parseFrom(data);
    }

    public static BinaryLog parseFrom(
        java.nio.ByteBuffer data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
        return PARSER.parseFrom(data, extensionRegistry);
    }

    public static BinaryLog parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
        return PARSER.parseFrom(data);
    }

    public static BinaryLog parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
        return PARSER.parseFrom(data, extensionRegistry);
    }

    public static BinaryLog parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
        return PARSER.parseFrom(data);
    }

    public static BinaryLog parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
        return PARSER.parseFrom(data, extensionRegistry);
    }

    public static BinaryLog parseFrom(java.io.InputStream input)
        throws java.io.IOException {
        return com.google.protobuf.GeneratedMessageV3
            .parseWithIOException(PARSER, input);
    }

    public static BinaryLog parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
        return com.google.protobuf.GeneratedMessageV3
            .parseWithIOException(PARSER, input, extensionRegistry);
    }

    public static BinaryLog parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
        return com.google.protobuf.GeneratedMessageV3
            .parseDelimitedWithIOException(PARSER, input);
    }

    public static BinaryLog parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
        return com.google.protobuf.GeneratedMessageV3
            .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }

    public static BinaryLog parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
        return com.google.protobuf.GeneratedMessageV3
            .parseWithIOException(PARSER, input);
    }

    public static BinaryLog parseFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
        return com.google.protobuf.GeneratedMessageV3
            .parseWithIOException(PARSER, input, extensionRegistry);
    }

    @java.lang.Override
    public Builder newBuilderForType() {
        return newBuilder();
    }

    public static Builder newBuilder() {
        return DEFAULT_INSTANCE.toBuilder();
    }

    public static Builder newBuilder(BinaryLog prototype) {
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
     * Protobuf type {@code dumper.BinaryLog}
     */
    public static final class Builder extends
        com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
        // @@protoc_insertion_point(builder_implements:dumper.BinaryLog)
        BinaryLogOrBuilder {
        public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
            return DumperServer.internal_static_dumper_BinaryLog_descriptor;
        }

        @java.lang.Override
        protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
            return DumperServer.internal_static_dumper_BinaryLog_fieldAccessorTable
                .ensureFieldAccessorsInitialized(
                    BinaryLog.class, BinaryLog.Builder.class);
        }

        // Construct using com.alibaba.tddl.rpc.cdc.BinaryLog.newBuilder()
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
            logName_ = "";

            fileSize_ = 0L;

            return this;
        }

        @java.lang.Override
        public com.google.protobuf.Descriptors.Descriptor
        getDescriptorForType() {
            return DumperServer.internal_static_dumper_BinaryLog_descriptor;
        }

        @java.lang.Override
        public BinaryLog getDefaultInstanceForType() {
            return BinaryLog.getDefaultInstance();
        }

        @java.lang.Override
        public BinaryLog build() {
            BinaryLog result = buildPartial();
            if (!result.isInitialized()) {
                throw newUninitializedMessageException(result);
            }
            return result;
        }

        @java.lang.Override
        public BinaryLog buildPartial() {
            BinaryLog result = new BinaryLog(this);
            result.logName_ = logName_;
            result.fileSize_ = fileSize_;
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
            if (other instanceof BinaryLog) {
                return mergeFrom((BinaryLog) other);
            } else {
                super.mergeFrom(other);
                return this;
            }
        }

        public Builder mergeFrom(BinaryLog other) {
            if (other == BinaryLog.getDefaultInstance()) {
                return this;
            }
            if (!other.getLogName().isEmpty()) {
                logName_ = other.logName_;
                onChanged();
            }
            if (other.getFileSize() != 0L) {
                setFileSize(other.getFileSize());
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
            BinaryLog parsedMessage = null;
            try {
                parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
            } catch (com.google.protobuf.InvalidProtocolBufferException e) {
                parsedMessage = (BinaryLog) e.getUnfinishedMessage();
                throw e.unwrapIOException();
            } finally {
                if (parsedMessage != null) {
                    mergeFrom(parsedMessage);
                }
            }
            return this;
        }

        private java.lang.Object logName_ = "";

        /**
         * <code>string logName = 1;</code>
         *
         * @return The logName.
         */
        public java.lang.String getLogName() {
            java.lang.Object ref = logName_;
            if (!(ref instanceof java.lang.String)) {
                com.google.protobuf.ByteString bs =
                    (com.google.protobuf.ByteString) ref;
                java.lang.String s = bs.toStringUtf8();
                logName_ = s;
                return s;
            } else {
                return (java.lang.String) ref;
            }
        }

        /**
         * <code>string logName = 1;</code>
         *
         * @return The bytes for logName.
         */
        public com.google.protobuf.ByteString
        getLogNameBytes() {
            java.lang.Object ref = logName_;
            if (ref instanceof String) {
                com.google.protobuf.ByteString b =
                    com.google.protobuf.ByteString.copyFromUtf8(
                        (java.lang.String) ref);
                logName_ = b;
                return b;
            } else {
                return (com.google.protobuf.ByteString) ref;
            }
        }

        /**
         * <code>string logName = 1;</code>
         *
         * @param value The logName to set.
         * @return This builder for chaining.
         */
        public Builder setLogName(
            java.lang.String value) {
            if (value == null) {
                throw new NullPointerException();
            }

            logName_ = value;
            onChanged();
            return this;
        }

        /**
         * <code>string logName = 1;</code>
         *
         * @return This builder for chaining.
         */
        public Builder clearLogName() {

            logName_ = getDefaultInstance().getLogName();
            onChanged();
            return this;
        }

        /**
         * <code>string logName = 1;</code>
         *
         * @param value The bytes for logName to set.
         * @return This builder for chaining.
         */
        public Builder setLogNameBytes(
            com.google.protobuf.ByteString value) {
            if (value == null) {
                throw new NullPointerException();
            }
            checkByteStringIsUtf8(value);

            logName_ = value;
            onChanged();
            return this;
        }

        private long fileSize_;

        /**
         * <code>int64 fileSize = 2;</code>
         *
         * @return The fileSize.
         */
        @java.lang.Override
        public long getFileSize() {
            return fileSize_;
        }

        /**
         * <code>int64 fileSize = 2;</code>
         *
         * @param value The fileSize to set.
         * @return This builder for chaining.
         */
        public Builder setFileSize(long value) {

            fileSize_ = value;
            onChanged();
            return this;
        }

        /**
         * <code>int64 fileSize = 2;</code>
         *
         * @return This builder for chaining.
         */
        public Builder clearFileSize() {

            fileSize_ = 0L;
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

        // @@protoc_insertion_point(builder_scope:dumper.BinaryLog)
    }

    // @@protoc_insertion_point(class_scope:dumper.BinaryLog)
    private static final BinaryLog DEFAULT_INSTANCE;

    static {
        DEFAULT_INSTANCE = new BinaryLog();
    }

    public static BinaryLog getDefaultInstance() {
        return DEFAULT_INSTANCE;
    }

    private static final com.google.protobuf.Parser<BinaryLog>
        PARSER = new com.google.protobuf.AbstractParser<BinaryLog>() {
        @java.lang.Override
        public BinaryLog parsePartialFrom(
            com.google.protobuf.CodedInputStream input,
            com.google.protobuf.ExtensionRegistryLite extensionRegistry)
            throws com.google.protobuf.InvalidProtocolBufferException {
            return new BinaryLog(input, extensionRegistry);
        }
    };

    public static com.google.protobuf.Parser<BinaryLog> parser() {
        return PARSER;
    }

    @java.lang.Override
    public com.google.protobuf.Parser<BinaryLog> getParserForType() {
        return PARSER;
    }

    @java.lang.Override
    public BinaryLog getDefaultInstanceForType() {
        return DEFAULT_INSTANCE;
    }

}

