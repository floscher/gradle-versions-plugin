/*
 * Copyright 2012 Ben Manes. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.benmanes.gradle.versions.updates
import com.github.benmanes.gradle.versions.reporter.JsonReporter
import com.github.benmanes.gradle.versions.reporter.PlainTextReporter
import com.github.benmanes.gradle.versions.reporter.XmlReporter
import groovy.transform.TupleConstructor
/**
 * A reporter for the dependency updates results.
 *
 * @author Ben Manes (ben.manes@gmail.com)
 */
@TupleConstructor
class DependencyUpdatesReporter {
  /** The project evaluated against. */
  def project
  /** The revision strategy evaluated with. */
  def revision
  /** The formatter strategy evaluated with. */
  def formatter
  /** The output strategy evaluated with. */
  def output

  /** The current versions of each dependency declared in the project(s). */
  def currentVersions
  /** The latest versions of each dependency (as scoped by the revision level). */
  def latestVersions

  /** The dependencies that are up to date (same as latest found). */
  def upToDateVersions
  /** The dependencies that exceed the latest found (e.g. may not want SNAPSHOTs). */
  def downgradeVersions
  /** The dependencies where upgrades were found (below latest found). */
  def upgradeVersions
  /** The dependencies that could not be resolved. */
  def unresolved

  private static Object mutex = new Object();

  /** Writes the report to the console. */
  def writeToConsole() {
    synchronized (mutex) {
      writeTo(System.out)
    }
  }

  /** Writes the report to the file. */
  def writeToFile(fileName) {
    def printStream = new PrintStream(fileName)
    try {
      writeTo(printStream)
    } finally {
      printStream.close()
    }
  }

  /** Writes the report to the print stream. The stream is not automatically closed. */
  def writeTo(printStream) {
    def reporter

    switch (formatter) {
      case 'json':
        reporter = new JsonReporter(project, revision, currentVersions, latestVersions,
            upToDateVersions, downgradeVersions, upgradeVersions, unresolved)
        break;
      case 'xml':
        reporter = new XmlReporter(project, revision, currentVersions, latestVersions,
            upToDateVersions, downgradeVersions, upgradeVersions, unresolved)
        break;
      default:
        reporter = new PlainTextReporter(project, revision, currentVersions, latestVersions,
            upToDateVersions, downgradeVersions, upgradeVersions, unresolved)
    }
    reporter.writeTo(printStream)
  }

}
