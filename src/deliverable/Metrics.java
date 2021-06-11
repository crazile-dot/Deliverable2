package deliverable;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.HashSet;
import java.util.List;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.Edit;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.errors.InvalidObjectIdException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.EmptyTreeIterator;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.util.io.DisabledOutputStream;
import org.json.JSONException;
import org.json.JSONObject;

public class Metrics {

public static void getAddedDeleted(Repository repository, Release release, JSONObject jsonDataset) throws IOException, JSONException{
		
		int linesAdded;
		int linesDeleted;
		int linesReplaced;
		int filesChanged = 0;		
		
		for(Commit commit: release.getRCommitList()) {
			try {
				linesAdded = 0;
				linesDeleted = 0;
				linesReplaced = 0;
			    DiffFormatter df = new DiffFormatter(DisabledOutputStream.INSTANCE);
			    df.setRepository(repository);
			    df.setDiffComparator(RawTextComparator.DEFAULT);
			    df.setDetectRenames(true);
			    List<DiffEntry> diffs = getDiffs(repository, commit);
			    filesChanged = diffs.size();
			    for (DiffEntry diff : diffs) {
			    	/*if (!diff.getChangeType().toString().equals("ADD")) {
			    		System.out.println(diff.getChangeType().toString());
			    	}*/
			    	if(diff.toString().contains(".java")) {
				    	//System.out.println(diff.toString());
				        for(Edit edit : df.toFileHeader(diff).toEditList()) {
				        	//System.out.println(edit.toString());
				        	if (edit.getBeginA() == edit.getEndA()) {
					            linesAdded += edit.getEndB() - edit.getBeginB();
				        	} else if (edit.getBeginB() == edit.getEndB()) {
					            linesDeleted += edit.getEndA() - edit.getBeginA();
				        	} else {
				        		linesReplaced += edit.getEndA() - edit.getBeginA();
				        	}
				        }
				        jsonDataset.put("Name file", diff.toString().substring(14));
				    	jsonDataset.put("Lines added", linesAdded);		
				    	jsonDataset.put("Lines deleted", linesDeleted);
				    	jsonDataset.put("Lines replaced", linesReplaced);
			    	}
			    	
			    	//System.out.println(jsonDataset);
			    }
		
		  			            
			} catch (IOException e1) {
			    throw new RuntimeException(e1);
			}
			
		}
	}

	
	public static long countLines(String fileName) {

	      long lines = 0;
	      try (BufferedReader reader = new BufferedReader(new FileReader("C:\\Users\\crazile\\git\\bookkeeper\\" + fileName))) {
	          while (reader.readLine() != null) lines++;
	      } catch (IOException e) {
	          e.printStackTrace();
	      }
	      return lines;

	 }
	
	
	public static RevCommit castToRevCommit(Repository repository, Commit commit) throws IOException {
		RevCommit rev = null;
		ObjectId commitId = ObjectId.fromString(commit.getId());
		//System.out.println("qua: " + commitId.getName());
		try (RevWalk revWalk = new RevWalk(repository)) {
		  rev = revWalk.parseCommit(commitId);
		  //System.out.println("qui: " + rev.getName());
		}
		return rev;
	}
	
	public static List<DiffEntry> getDiffs(Repository repository, Commit commit) throws IOException {
		List<DiffEntry> diffs;
		RevCommit rev = castToRevCommit(repository, commit);
		DiffFormatter df = new DiffFormatter(DisabledOutputStream.INSTANCE);
		df.setRepository(repository);
		df.setDiffComparator(RawTextComparator.DEFAULT);
		df.setContext(0);
		df.setDetectRenames(true);
		if (rev.getParentCount() != 0) {
			RevCommit parent = (RevCommit) rev.getParent(0).getId();
			System.out.println("Parent: " + parent.getId().toString());
			diffs = df.scan(parent.getTree(), rev.getTree());
		} else {
			RevWalk rw = new RevWalk(repository);
			ObjectReader reader = rw.getObjectReader();
			diffs = df.scan(new EmptyTreeIterator(), new CanonicalTreeParser(null, reader, rev.getTree()));
		}
		
		return diffs;
 }
	
	public static void getMetrics(List<Commit> commitList, List<Release> releaseList) throws IOException, JSONException, NoHeadException, GitAPIException, ParseException{
				
		JSONObject jsonDataset = new JSONObject();
		HashSet<String> countDevelopers;
        List<DiffEntry> diffs;
		Iterable<RevCommit> logs;
		//List<RevCommit> commitList = new ArrayList<>();
		int countDevs;
		int countAdded;
		Git git = Git.open(new File("C:\\Users\\crazile\\git\\bookkeeper\\.git"));

		
		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		Repository repository = builder.setGitDir(new File("C:\\Users\\crazile\\git\\bookkeeper\\.git"))
		  .readEnvironment() // scan environment GIT_* variables
		  .findGitDir() // scan up the file system tree
		  .build();
		Ref head = repository.exactRef("HEAD");

        // a RevWalk allows to walk over commits based on some filtering that is defined
        RevWalk walk = new RevWalk(repository);

        RevCommit newCommit = walk.parseCommit(head.getObjectId());
        //RevCommit oldCommit = walk.parseCommit(newCommit.getParent(0).getId());
        RevTree tree = newCommit.getTree();
        //System.out.println("Having tree: " + tree);

        // now use a TreeWalk to iterate over all files in the Tree recursively
        // you can set Filters to narrow down the results if needed
        TreeWalk treeWalk = new TreeWalk(repository);
        treeWalk.addTree(tree);
        treeWalk.setRecursive(true);
        /*while (treeWalk.next()) {
            //System.out.println("found: " + treeWalk.getPathString());
            if (treeWalk.getPathString().endsWith(".java")) {
                jsonDataset = new JSONObject();
                countDevelopers = new HashSet<String>();
                countDevs = 0;
                countAdded = 0;
                logs = new Git(repository).log().addPath(treeWalk.getPathString()).call();
                //System.out.println(logs);
                //int i = 0;
                /*for (RevCommit rev: logs) {
                	if (commitList.isEmpty() || !containsRev(rev, commitList)) {
                		commitList.add(rev);
                }
                for (RevCommit rev: logs) {
                //for (int j = 0; j < commitList.size(); j++) {
	                countDevelopers.add(rev.getAuthorIdent().getEmailAddress());
	                countDevs++;
	                //System.out.println(commitList.get(j).getName());
	                //i++;
	                //countAdded = getAdded(repository, newCommit, rev);
	                //countAdded++;
	                //jsonDataset.put("LOC_added", countAdded);      		    
                }
                /*if (i == 10) {
                    System.exit(0);
                }
                jsonDataset.put("FileName", treeWalk.getPathString());
                jsonDataset.put("CountDevelopers", countDevelopers.size());
                jsonDataset.put("CountCommits", countDevs);
                jsonDataset.put("LOC", countLines(treeWalk.getPathString()));
                //jsonDataset.put("LOC_added", getAdded(repository, newCommit,));
                //commitDetails.put(jsonDataset);
                //System.out.println("Json: " + jsonDataset);
                //System.out.println("LOC: " + treeWalk.getPathString());
                }
        }*/
       
        for(Release r: releaseList) {
            getAddedDeleted(repository, r, jsonDataset);
            //System.out.println(jsonDataset);        
        }
        
        //getAddedDeleted();
        		
		/*ObjectId commitId = ObjectId.fromString("61ddb9f3469cc307ab39ac5d2fb946eed59c003b");
		try (RevWalk revWalk = new RevWalk(repository)) {
		  RevCommit commit = revWalk.parseCommit(commitId);
		}*/
		
     }
}
