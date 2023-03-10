/* -*- java -*-
# =========================================================================== #
#                                                                             #
#                         Copyright (C) KNAPP AG                              #
#                                                                             #
#       The copyright to the computer program(s) herein is the property       #
#       of Knapp.  The program(s) may be used   and/or copied only with       #
#       the  written permission of  Knapp  or in  accordance  with  the       #
#       terms and conditions stipulated in the agreement/contract under       #
#       which the program(s) have been supplied.                              #
#                                                                             #
# =========================================================================== #
*/

package com.knapp.codingcontest.core;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.knapp.codingcontest.solution.Solution;

/**
 * Helper class to create zip-file for upload
 */
public final class PrepareUpload {
  private static final String PATH_OUTPUT;

  // ----------------------------------------------------------------------------
  // ............................................................................

  static {
    try {
      PATH_OUTPUT = new File("./").getCanonicalPath();
    } catch (final IOException e) {
      throw new RuntimeException(e);
    }
  }

  // ============================================================================

  public static final String FILENAME_RESULT = "result.csv";
  public static final String FILENAME_META_PROPERTIES = "kcc.properties";
  public static final String FILENAME_UPLOAD_ZIP = "upload.zip";

  // ----------------------------------------------------------------------------

  private PrepareUpload() {
    // avoid construction
  }

  // ----------------------------------------------------------------------------

  public static void createZipFile(final Solution solution, final OperationsInternal operations) throws Exception {
    final File basedir = new File(PrepareUpload.PATH_OUTPUT);
    final String zipFileName = PrepareUpload.PATH_OUTPUT + File.separator + PrepareUpload.FILENAME_UPLOAD_ZIP;
    final File zipFile = new File(zipFileName);
    if (zipFile.exists()) {
      zipFile.delete();
    }

    ZipOutputStream archive = null;
    try {
      archive = new ZipOutputStream(new FileOutputStream(zipFile));

      final String resultsFileName = PrepareUpload.PATH_OUTPUT + File.separator + PrepareUpload.FILENAME_RESULT;
      PrepareUpload.storeResult(resultsFileName, operations.result());
      PrepareUpload.addFile(archive, basedir, new File(resultsFileName));

      final String propertiesFileName = PrepareUpload.PATH_OUTPUT + File.separator + PrepareUpload.FILENAME_META_PROPERTIES;
      PrepareUpload.addInputStream(archive, basedir, new File(propertiesFileName), PrepareUpload.properties(solution));

      PrepareUpload.add(archive, basedir, new File("src"));
    } finally {
      if (archive != null) {
        archive.close();
      }
    }
  }

  private static void storeResult(final String resultsFileName, final Iterable<WarehouseOperation> result) throws IOException {
    final Writer fw = new FileWriter(resultsFileName);
    BufferedWriter writer = null;
    try {
      writer = new BufferedWriter(fw);
      for (final WarehouseOperation warehouseOperation : result) {
        writer.append(warehouseOperation.toResultString());
        writer.newLine();
      }
    } finally {
      PrepareUpload.close(writer);
      PrepareUpload.close(fw);
    }
  }

  // ----------------------------------------------------------------------------

  private static void add(final ZipOutputStream archive, final File basedir, final File file) throws IOException {
    if (file.isDirectory()) {
      if (PrepareUpload.shouldAddDirectory(basedir, file)) {
        PrepareUpload.addDirectory(archive, basedir, file);
      }
    } else {
      if (PrepareUpload.shouldAddFile(basedir, file)) {
        PrepareUpload.addFile(archive, basedir, file);
      }
    }
  }

  // ............................................................................

  @SuppressWarnings("unused")
  private static boolean shouldAddDirectory(final File basedir, final File file) {
    return !"META-INF".equals(file.getName());
  }

  @SuppressWarnings("unused")
  private static boolean shouldAddFile(final File basedir, final File file) {
    return !file.getName().equals(PrepareUpload.FILENAME_UPLOAD_ZIP);
  }

  // ----------------------------------------------------------------------------

  private static void addDirectory(final ZipOutputStream archive, final File basedir, final File file) throws IOException {
    if (!basedir.equals(file)) {
      String name = file.getAbsolutePath().replace("\\", "/");
      if (!name.isEmpty()) {
        if (!name.endsWith("/")) {
          name += "/";
        }

        final ZipEntry entry = new ZipEntry(name.substring(basedir.getAbsolutePath().length() + 1));
        entry.setTime(file.lastModified());
        archive.putNextEntry(entry);
        archive.closeEntry();
      }
    }

    for (final File nestedFile : file.listFiles()) {
      PrepareUpload.add(archive, basedir, nestedFile);
    }
  }

  // ............................................................................

  private static void addFile(final ZipOutputStream archive, final File basedir, final File file)
      throws IOException, FileNotFoundException {
    BufferedInputStream in = null;
    try {
      final ZipEntry entry = new ZipEntry(
          file.getAbsolutePath().replace("\\", "/").substring(basedir.getAbsolutePath().length() + 1));
      entry.setTime(file.lastModified());
      archive.putNextEntry(entry);

      in = new BufferedInputStream(new FileInputStream(file));
      PrepareUpload.copyContent(in, archive);

      archive.closeEntry();
    } finally {
      if (in != null) {
        in.close();
      }
    }
  }

  private static void addInputStream(final ZipOutputStream archive, final File basedir, final File file,
      final InputStream inputStream) throws IOException, FileNotFoundException {
    BufferedInputStream in = null;
    try {
      final ZipEntry entry = new ZipEntry(
          file.getAbsolutePath().replace("\\", "/").substring(basedir.getAbsolutePath().length() + 1));
      entry.setTime(file.lastModified());
      archive.putNextEntry(entry);

      in = new BufferedInputStream(inputStream);
      PrepareUpload.copyContent(in, archive);

      archive.closeEntry();
    } finally {
      if (in != null) {
        in.close();
      }
    }
  }

  // ----------------------------------------------------------------------------

  private static void copyContent(final InputStream in, final OutputStream out) throws IOException {
    final byte[] buffer = new byte[8192];
    for (int count = in.read(buffer); count >= 0; count = in.read(buffer)) {
      out.write(buffer, 0, count);
    }
  }

  // ----------------------------------------------------------------------------

  private static InputStream properties(final Solution solution) throws Exception {
    BufferedWriter writer = null;
    try {
      final ByteArrayOutputStream baos = new ByteArrayOutputStream();
      writer = new BufferedWriter(new OutputStreamWriter(baos, Charset.forName("ISO-8859-1")));
      writer.write("# -*- conf-javaprop -*-");
      writer.newLine();
      writer.write(String.format("participant  = %s", solution.getParticipantName().trim()));
      writer.newLine();
      writer.write(String.format("institution  = %s", solution.getParticipantInstitution().name()));
      writer.newLine();
      writer.write(String.format("technology   = java"));
      writer.newLine();
      writer.flush();
      return new ByteArrayInputStream(baos.toByteArray());
    } finally {
      if (writer != null) {
        writer.close();
      }
    }
  }

  // ----------------------------------------------------------------------------

  private static void close(final Closeable closeable) {
    if (closeable != null) {
      try {
        closeable.close();
      } catch (final IOException exception) {
        exception.printStackTrace(System.err);
      }
    }
  }

  // ----------------------------------------------------------------------------
}
