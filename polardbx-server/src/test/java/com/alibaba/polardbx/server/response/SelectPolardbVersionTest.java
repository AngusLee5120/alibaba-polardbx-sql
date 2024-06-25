package com.alibaba.polardbx.server.response;

import com.alibaba.polardbx.executor.utils.ExecUtils;
import com.alibaba.polardbx.gms.topology.DbTopologyManager;
import com.alibaba.polardbx.gms.util.MetaDbUtil;
import com.alibaba.polardbx.mock.MockPacketOutputProxy;
import com.alibaba.polardbx.net.packet.RowDataPacket;
import com.google.common.collect.Sets;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static com.alibaba.polardbx.server.response.SelectPolardbVersion.getFullProductionVersion;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SelectPolardbVersionTest {

    private static final String CHARSET = "utf8";

    private static RowDataPacket generatePacket() {
        RowDataPacket row = new RowDataPacket(3);
        return row;
    }

    @Test
    public void testParseVersion() {
        RowDataPacket packet = generatePacket();
        String type = "CN";
        final String cnVersion = "5.4.19";
        final String cnReleaseDate = "20240314";
        SelectPolardbVersion.addVersionWithReleaseDate(packet, type,
            cnVersion + "-" + cnReleaseDate, CHARSET);

        String typeInPacket = new String(packet.fieldValues.get(0));
        String versionInPacket = new String(packet.fieldValues.get(1));
        String releaseDateInPacket = new String(packet.fieldValues.get(2));
        Assert.assertEquals(type, typeInPacket);
        Assert.assertEquals(getFullProductionVersion(cnVersion), versionInPacket);
        Assert.assertEquals(cnReleaseDate, releaseDateInPacket);
    }

    /**
     * expect NULL for null version
     */
    @Test
    public void testParseNullVersion() {
        RowDataPacket packet = generatePacket();
        String type = "Columnar";
        SelectPolardbVersion.addVersionWithReleaseDate(packet, type, null, CHARSET);

        String typeInPacket = new String(packet.fieldValues.get(0));
        Assert.assertEquals(type, typeInPacket);
        Assert.assertNull(packet.fieldValues.get(1));
        Assert.assertNull(packet.fieldValues.get(2));
    }

    /**
     * test illegal version format
     * eg. without releaseDate
     */
    @Test
    public void testParseIllegalVersion() {
        RowDataPacket packet = generatePacket();
        String type = "Columnar";
        final String version = "5.4.19";
        SelectPolardbVersion.addVersionWithReleaseDate(packet, type, version, CHARSET);

        String typeInPacket = new String(packet.fieldValues.get(0));
        Assert.assertEquals(type, typeInPacket);
        String versionInPacket = new String(packet.fieldValues.get(1));
        Assert.assertEquals(getFullProductionVersion(version), versionInPacket);
        Assert.assertNull(packet.fieldValues.get(2));
    }

    @Test
    public void testPolardbVersion() throws SQLException {
        try (MockedStatic<ExecUtils> mockExecUtils = Mockito.mockStatic(ExecUtils.class);
            MockedStatic<MetaDbUtil> mockMetaDbUtils = Mockito.mockStatic(MetaDbUtil.class);
            MockedStatic<DbTopologyManager> mockedTopoManager = Mockito.mockStatic(DbTopologyManager.class)) {
            final String mockReleaseDate = "20240412";
            final String mockEngineVersion = "5.4.19";
            mockExecUtils.when(ExecUtils::getDnPolardbVersion).thenCallRealMethod();
            mockExecUtils.when(ExecUtils::getAllDnStorageId).thenReturn(Sets.newHashSet("mockDn"));
            Connection mockConnection = mock(Connection.class);
            mockedTopoManager.when(() -> DbTopologyManager.getConnectionForStorage(anyString()))
                .thenReturn(mockConnection);
            Statement statement = mock(Statement.class);
            when(mockConnection.createStatement()).thenReturn(statement);
            ResultSet resultSet = mock(ResultSet.class);
            when(statement.executeQuery(Mockito.anyString())).thenReturn(resultSet);
            when(resultSet.next()).thenReturn(true);
            when(resultSet.getString(1)).thenReturn(mockEngineVersion);
            when(resultSet.getString(2)).thenReturn(mockReleaseDate);

            String dnPolardbVersion = null;
            try {
                dnPolardbVersion = ExecUtils.getDnPolardbVersion();
            } catch (Exception e) {
                Assert.fail(e.getMessage());
            }
            Assert.assertNotNull(dnPolardbVersion);
            Assert.assertEquals(String.format("%s-%s", mockEngineVersion, mockReleaseDate), dnPolardbVersion);

            SelectPolardbVersion.addDnVersion(new MockPacketOutputProxy(), (byte) 1, "utf8");

            mockMetaDbUtils.when(MetaDbUtil::getGmsPolardbVersion)
                .thenReturn(String.format("%s-%s", mockEngineVersion, mockReleaseDate));
            SelectPolardbVersion.addGmsVersion(new MockPacketOutputProxy(), (byte) 1, "utf8");
        }

        try (MockedStatic<ExecUtils> mockExecUtils = Mockito.mockStatic(ExecUtils.class)) {
            mockExecUtils.when(ExecUtils::getDnPolardbVersion).thenCallRealMethod();
            String dnPolardbVersion = null;
            try {
                dnPolardbVersion = ExecUtils.getDnPolardbVersion();
                Assert.fail("Expect failed");
            } catch (Exception e) {
                Assert.assertTrue(e.getMessage().contains("Failed to get DN datasource"));
            }
            Assert.assertNull(dnPolardbVersion);
        }
    }
}
