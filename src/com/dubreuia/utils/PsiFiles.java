package com.dubreuia.utils;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import static com.dubreuia.SaveActionManager.LOGGER;

public class PsiFiles {

    private PsiFiles() {
        // static class
    }

    public static boolean isPsiFilePhysicallyInProject(Project project, PsiFile psiFile) {
        boolean psiFilePhysicallyInProject = isPsiFilePhysicallyInProject(project, psiFile.getParent());
        if (!psiFilePhysicallyInProject) {
            LOGGER.debug("File " + psiFile.getVirtualFile().getCanonicalPath() + " not in project " + project);
        }
        return psiFilePhysicallyInProject;
    }

    public static boolean isPsiFilePhysicallyInProject(Project project, PsiDirectory psiDirectory) {
        if (psiDirectory != null) {
            if (project.getBaseDir().equals(psiDirectory.getVirtualFile())) {
                return true;
            }
            return isPsiFilePhysicallyInProject(project, psiDirectory.getParent());
        }
        return false;
    }

    public static boolean isPsiFileExcluded(Project project, PsiFile psiFile, Set<String> exclusions) {
        final String fullPsiFileUrl = psiFile.getVirtualFile().getPresentableUrl();
        final String fullProjectUrl = project.getPresentableUrl();
        final String usableUrl = getUsableUrl(fullProjectUrl, fullPsiFileUrl);
        return null != usableUrl && isUrlExcluded(usableUrl, exclusions);
    }

    static String getUsableUrl(String projectUrl, String psiFileUrl) {
        if (null != projectUrl && psiFileUrl.contains(projectUrl)) {
            return psiFileUrl.substring(projectUrl.length() + 1);
        }
        return null;
    }

    static boolean isUrlExcluded(String url, Set<String> exclusions) {
        for (String exclusion : exclusions) {
            try {
                final Pattern pattern = Pattern.compile(exclusion);
                final Matcher matcher = pattern.matcher(url);
                if (matcher.matches()) {
                    return true;
                }
            } catch (PatternSyntaxException e) {
                return false;
            }
        }
        return false;
    }

}
