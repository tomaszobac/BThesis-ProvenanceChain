# Provenance chain backbone traversal utility
#### This is an implementation part of a bachelor thesis.
#### Author of the work: Tomáš Zobač
#### Bachelor thesis supervisor: RNDr. Rudolf Wittner

## Set-up
### Clone the repo
1. `Clone`/`Code`
2. Clone with HTTPS
3. In the console, run `git clone <copied_url>`

### Init simulation files
In order for the simulated environment to run, you need to pull the required files
1. Open the cloned repo in the console
2. Move to the submodule using `cd .\src\main\resources\bthesis-provenancechain-digpat`
3. Run `git submodule foreach git fetch --tags` (There should be no output)

4. Finish by running `git submodule update --init --recursive`
5. The `bthesis-provenancechain-digpat` submodule should now be populated with `.provn` files

## Building
The implementation uses a Maven Shade plugin for jar packaging

1. Open the cloned repo in the console
2. Run `mvn clean package`
3. Execute the created jar with `java -jar .\target\BThesis-ProvenanceChain-VERSION-shaded.jar`
    - alternatively, if the targeted environment doesn't have a JRE, you can create an exe installer
   using Java's prepackaged `jpackage` command line tool
## Omitting the simulated environment
In order to simulate the traversal, the implementation uses multiple classes and files
to provide the required objects for the traversing algorithm to work.

For clarity, these classes are moved to packages `bthesis.provenancechain.simulation` and `bthesis.metageneration`,
while the required files are located in the submodule `.\src\main\resources\bthesis-provenancechain-digpat` mentioned before

All of these can be removed as long as the classes needed are sufficiently replaced.