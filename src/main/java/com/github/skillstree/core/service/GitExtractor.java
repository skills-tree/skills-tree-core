package com.github.skillstree.core.service;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.util.io.DisabledOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GitExtractor {

    private final static Logger logger = LoggerFactory.getLogger(GitExtractor.class);

    private final File repoDir;

    public GitExtractor(File repoDir) {
        this.repoDir = repoDir;
        if (!repoDir.exists()) {
            if (!repoDir.mkdir()) {
                throw new IllegalArgumentException("Cannot create specified directory " + repoDir);
            }
        }
        logger.info("Repository directory: {}", repoDir);
    }

    public File getDirWithSkills() throws GitAPIException {
        try {
            Git.open(repoDir).pull().call();
        } catch (IOException e) {
            logger.info("Cannot open the repository: {}", e.getMessage());

            if (repoDir.exists()) {
                if (!deleteDirectory(repoDir)) {
                    logger.error("Cannot delete repository's directory {}", repoDir);
                }
            }

            Git.cloneRepository()
                    .setURI("https://github.com/ilyavy/skills.git") // TODO: config
                    .setDirectory(repoDir)
                    .setBranch("aws") // TODO: config
                    .setTimeout(10) // TODO: config
                    .call();
        }
        return repoDir;
    }

    private boolean deleteDirectory(File directoryToBeDeleted) {
        File[] files = directoryToBeDeleted.listFiles();
        if (files != null) {
            for (File file : files) {
                deleteDirectory(file);
            }
        }
        return directoryToBeDeleted.delete();
    }

    public String getLastCommitId() {
        try (Repository repository = Git.open(repoDir).getRepository()) {
            ObjectId head = repository.resolve(Constants.HEAD);
            return head.getName();

        } catch (Exception e) {
            logger.error("Cannot get last commit id", e);
            return null;
        }
    }

    public List<String> getListOfChangedFilesAfter(String commitId) {
        List<String> result = new ArrayList<>();

        try (Repository repository = Git.open(repoDir).getRepository()) {
            RevWalk rw = new RevWalk(repository);
            ObjectId head = repository.resolve(Constants.HEAD);
            RevCommit commit = rw.parseCommit(head);
            RevCommit parent = rw.parseCommit(ObjectId.fromString(commitId));

            DiffFormatter df = new DiffFormatter(DisabledOutputStream.INSTANCE);
            df.setRepository(repository);
            df.setDiffComparator(RawTextComparator.DEFAULT);
            df.setDetectRenames(true);

            List<DiffEntry> diffs = df.scan(parent.getTree(), commit.getTree());
            for (DiffEntry diff : diffs) {
                result.add(diff.getNewPath());
                logger.info("Diffs found: {}", MessageFormat.format("{0} {1} {2}",
                        diff.getChangeType().name(), diff.getNewMode().getBits(), diff.getNewPath()));
            }
        } catch (Exception e) {
            logger.error("Cannot obtain list of changed files after commit {}", commitId, e);
        }

        return result;
    }
}
