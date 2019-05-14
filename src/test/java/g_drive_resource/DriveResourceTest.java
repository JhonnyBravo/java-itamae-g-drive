package g_drive_resource;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;

import g_auth_resource.CredentialResource;
import g_auth_resource.ReaderResource;
import g_auth_resource.ScopesResource;

/**
 * {@link g_drive_resource.DriveResource} の単体テスト。
 */
@RunWith(Enclosed.class)
public class DriveResourceTest {
    public static class 親ディレクトリを指定しない場合のアップロードテスト {
        private ScopesResource sr;
        private ReaderResource rr;
        private DriveResource dr;
        private File file;

        /**
         * @throws java.lang.Exception
         */
        @Before
        public void setUp() throws Exception {
            // 操作権限の設定
            sr = new ScopesResource();
            sr.addScope(DriveScopes.DRIVE);
            List<String> scopes = sr.getScopes();

            // 認証ファイルの読込
            rr = new ReaderResource("src/test/resources/credentials.json");
            rr.openContext();
            InputStreamReader reader = (InputStreamReader) rr.getContext();

            dr = new DriveResource(new CredentialResource(reader, scopes), "g-drive-resource");
            rr.closeContext();
        }

        /**
         * @throws java.lang.Exception
         */
        @After
        public void tearDown() throws Exception {
            dr.delete(file.getId());
        }

        @Test
        public final void ディレクトリを作成できること() {
            file = dr.create("src/test");
            assertThat(dr.getCode(), is(2));
            assertThat(file.getMimeType(), is("application/vnd.google-apps.folder"));
            assertThat(file.getName(), is("test"));

            file = dr.getFileById(file.getId());
            assertThat(dr.getCode(), is(2));
            assertThat(file.getName(), is("test"));
            assertThat(file.getMimeType(), is("application/vnd.google-apps.folder"));
        }

        @Test
        public final void ファイルを作成できること() {
            file = dr.create("src/test/resources/test.txt");
            assertThat(dr.getCode(), is(2));
            assertThat(file.getMimeType(), is("text/plain"));
            assertThat(file.getName(), is("test.txt"));

            file = dr.getFileById(file.getId());
            assertThat(dr.getCode(), is(2));
            assertThat(file.getMimeType(), is("text/plain"));
            assertThat(file.getName(), is("test.txt"));
        }
    }

    public static class 親ディレクトリを指定する場合のアップロードテスト {
        private ScopesResource sr;
        private ReaderResource rr;
        private DriveResource dr;
        private File file;
        private File parent;

        /**
         * @throws java.lang.Exception
         */
        @Before
        public void setUp() throws Exception {
            // 操作権限の設定
            sr = new ScopesResource();
            sr.addScope(DriveScopes.DRIVE);
            List<String> scopes = sr.getScopes();

            // 認証ファイルの読込
            rr = new ReaderResource("src/test/resources/credentials.json");
            rr.openContext();
            InputStreamReader reader = (InputStreamReader) rr.getContext();

            dr = new DriveResource(new CredentialResource(reader, scopes), "g-drive-resource");
            rr.closeContext();

            parent = dr.create("src/test");
        }

        /**
         * @throws java.lang.Exception
         */
        @After
        public void tearDown() throws Exception {
            dr.delete(parent.getId());
        }

        @Test
        public void ディレクトリを作成できること() {
            dr.setParentId(parent.getId());
            file = dr.create("src/test/resources");

            assertThat(dr.getCode(), is(2));
            assertThat(file.getMimeType(), is("application/vnd.google-apps.folder"));
            assertThat(file.getName(), is("resources"));

            List<String> parents = file.getParents();
            assertThat(parents.size(), is(1));
            assertThat(parents.get(0), is(parent.getId()));
        }

        @Test
        public void ファイルを作成できること() {
            dr.setParentId(parent.getId());
            file = dr.create("src/test/resources/test.txt");

            assertThat(dr.getCode(), is(2));
            assertThat(file.getMimeType(), is("text/plain"));
            assertThat(file.getName(), is("test.txt"));

            List<String> parents = file.getParents();
            assertThat(parents.size(), is(1));
            assertThat(parents.get(0), is(parent.getId()));
        }
    }

    public static class 親ディレクトリを指定しない場合のファイル一覧取得テスト {
        private ScopesResource sr;
        private ReaderResource rr;
        private DriveResource dr;
        private File file;
        private File directory;

        /**
         * @throws java.lang.Exception
         */
        @Before
        public void setUp() throws Exception {
            // 操作権限の設定
            sr = new ScopesResource();
            sr.addScope(DriveScopes.DRIVE);
            List<String> scopes = sr.getScopes();

            // 認証ファイルの読込
            rr = new ReaderResource("src/test/resources/credentials.json");
            rr.openContext();
            InputStreamReader reader = (InputStreamReader) rr.getContext();

            dr = new DriveResource(new CredentialResource(reader, scopes), "g-drive-resource");
            rr.closeContext();

            file = dr.create("src/test/resources/test.txt");
            directory = dr.create("src/test");
        }

        /**
         * @throws java.lang.Exception
         */
        @After
        public void tearDown() throws Exception {
            dr.delete(file.getId());
            dr.delete(directory.getId());
        }

        @Test
        public final void 事前作成したファイルとディレクトリのファイルIDがリストに含まれていること() {
            List<File> files = dr.getFiles();
            assertThat(dr.getCode(), is(2));

            List<String> idList = new ArrayList<String>();

            for (File f : files) {
                idList.add(f.getId());
            }

            assertThat(idList.contains(file.getId()), is(true));
            assertThat(idList.contains(directory.getId()), is(true));
        }
    }

    public static class 親ディレクトリを指定する場合のファイル一覧取得テスト {
        private ScopesResource sr;
        private ReaderResource rr;
        private DriveResource dr;
        private File file;
        private File directory;
        private File parent;

        /**
         * @throws java.lang.Exception
         */
        @Before
        public void setUp() throws Exception {
            // 操作権限の設定
            sr = new ScopesResource();
            sr.addScope(DriveScopes.DRIVE);
            List<String> scopes = sr.getScopes();

            // 認証ファイルの読込
            rr = new ReaderResource("src/test/resources/credentials.json");
            rr.openContext();
            InputStreamReader reader = (InputStreamReader) rr.getContext();

            dr = new DriveResource(new CredentialResource(reader, scopes), "g-drive-resource");
            rr.closeContext();

            parent = dr.create("src/test");
            dr.setParentId(parent.getId());

            directory = dr.create("src/test/resources");
            file = dr.create("src/test/resources/test.txt");
        }

        /**
         * @throws java.lang.Exception
         */
        @After
        public void tearDown() throws Exception {
            dr.delete(parent.getId());
        }

        @Test
        public final void 指定した親ディレクトリの配下に存在するファイルとディレクトリのみが取得されていること() {
            List<File> files = dr.getFiles();
            assertThat(dr.getCode(), is(2));
            assertThat(files.size(), is(2));

            List<String> idList = new ArrayList<String>();

            for (File f : files) {
                List<String> parents = f.getParents();

                for (String p : parents) {
                    assertThat(p, is(parent.getId()));
                }

                idList.add(f.getId());
            }

            assertThat(idList.contains(file.getId()), is(true));
            assertThat(idList.contains(directory.getId()), is(true));
        }
    }

    public static class ダウンロードテスト {
        private ScopesResource sr;
        private ReaderResource rr;
        private DriveResource dr;

        private File remoteFile;
        private File remoteDirectory;

        private java.io.File localFile;
        private java.io.File localDirectory;

        /**
         * @throws java.lang.Exception
         */
        @Before
        public void setUp() throws Exception {
            // 操作権限の設定
            sr = new ScopesResource();
            sr.addScope(DriveScopes.DRIVE);
            List<String> scopes = sr.getScopes();

            // 認証ファイルの読込
            rr = new ReaderResource("src/test/resources/credentials.json");
            rr.openContext();
            InputStreamReader reader = (InputStreamReader) rr.getContext();

            dr = new DriveResource(new CredentialResource(reader, scopes), "g-drive-resource");
            rr.closeContext();

            remoteDirectory = dr.create("src/test/resources");
            remoteFile = dr.create("src/test/resources/test.txt");
        }

        /**
         * @throws java.lang.Exception
         */
        @After
        public void tearDown() throws Exception {
            dr.delete(remoteDirectory.getId());
            dr.delete(remoteFile.getId());
        }

        @Test
        public final void ディレクトリのダウンロードができること() {
            localDirectory = dr.download("src/test/resources", remoteDirectory.getId());
            assertThat(dr.getCode(), is(2));
            assertThat(localDirectory.isDirectory(), is(true));
            assertThat(localDirectory.getName(), is("resources"));
            localDirectory.delete();
        }

        @Test
        public final void ファイルのダウンロードができること() {
            localFile = dr.download("src/test", remoteFile.getId());
            assertThat(dr.getCode(), is(2));
            assertThat(localFile.isFile(), is(true));
            assertThat(localFile.getName(), is("test.txt"));
            localFile.delete();
        }
    }
}
