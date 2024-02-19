# Change Log

## [Unreleased]

### Added

- Processing static files - avatars for users.

### Changed

- Use the new split file format
- Removing a color category from a custom category without a unique palette key.
- Removing a custom category from a file object without specifying a category.
- Access to view all users for regular roles.

### Fixed

- Fix the creation of an additional line in the database when installing a new color palette for a custom category.
- Fix the creation of an additional line in the database when setting a new category for a file object.
- Fix getting the date from the comment
- Fix user unlocks.

## [0.0.13] 2024-02-17

### Added

- Add owner information when obtaining more detailed information about a file object.
- Rights to file objects:
    - Allow the user to download all file directories that are granted rights to him.
    - Remove multiple granted rights to a file object.
    - Add multiple granted rights to a file object.
    - When receiving granted rights to a file object, also receive the real name of the file object.
    - Allow the user to view the rights assigned to him on a file object
- Search for file objects. Search criteria:
  Corresponds to the nesting level of the directory.
  The owner of the file object.
  Rights assigned.
  File object type.

### Fixed

- Rights to file objects:
    - Basic rights are not created when issuing to the entire directory.
    - The user login is not added to files when granting rights to the entire directory.
- File objects of type file are created with a hash extension in the system name.
- Fix vulnerability CVE-2022-25857 7.5.
- The archive does not include several files that are located in the same directory if you download them selectively.

## [0.0.12] 2023-11-05

### Added

- Java update from 17 to 21.
- The date the comment was created for the file object.
- The date the file object was created.
- Add custom color palette ID when getting file object and custom category.
- The first prototype of the complex core of the system:
    - Recovering files that have been fragmented.
    - Downloading a file that is fragmented.
    - Splitting a file that has been downloaded.
    - Adding file fragmentation to atomic mode.
- Creating a tree structure of comments for file objects.

### Fixed

- It is impossible to obtain information about file objects in manager.
- Downloading a file that has a format in the system, but does not have data transferred to the API.

## [0.0.11] 2023-10-04

### Added

- Close the method for creating a user in the system and provide this opportunity only to administrators.
- Uploading an avatar for the user.
- Integrate a new method from the CreepTenuousImplants module.
- Creating additional API methods:
    - Obtaining information about selected file objects by system names.
- Obtaining information about a user category when receiving file objects.
- Obtaining information whether the user is blocked or not.
- Getting regular directory names in the manager/directory API method.

## [0.0.10] 2023-08-26

### Added

- Carry out general cleaning + tests, tests and more tests.
- Add custom color schemes:
    - Set the color palette for custom categories.
    - Add custom colors for objects of type - category.
- System for distributing file objects when deleting a user:
    - Add exceptions for certain file objects.
- Categories for file objects for use by users:
    - The entities themselves.
    - Linking file objects to a category.
- Creating shortcuts for file objects.
- Configure API methods to receive all user settings.
- Improve support for downloading file objects:
    - Files.
    - Catalogs.

## [0.0.9] 2023-07-17

### Added

- System for distributing file objects when deleting a user:
    - Custom settings for the user (you can choose one of two):
        - Transferring objects to the designated user.
        - Delete them if necessary (has the highest priority).
- Integration with the new CreepTenuousImplants module.
- Basic features for users:
    - Comments for a file object.
    - Freezing file objects (deactivation) (First prototype).
    - View issued rights to one object.
    - View all granted rights to objects.

## [0.0.8] 2023-07-02

### Added

- User improvements:
    - List of users (admin mode).
    - User profile (personal account).
    - Removing users (admin mode).
    - Sign Out.
    - Blocking a user (admin mode).
    - Unblocking a user (admin mode).
    - Blocking users for a while (admin mode).
- Set events for deleting a user - if it is deleted, then all file objects and everything associated with them are also
  deleted (issued and assigned rights).
- Checking for uniqueness of file system object names when creating them - for each user.
- User rights support:
    - Grant certain rights to the user for the entire folder (for all content) (set the user login for a file system
      object).
    - Remove certain rights for a user for the entire folder (for all content).
- Improve atomic mode support:
    - Add the ability to handle an exception by a specified type (class, subclass or interface), and not just by the
      class of the exception.

## [0.0.7] 2023-06-21

- Add a new API method - fetching file system objects and then downloading them in a zip archive (keep the old method -
  /api/v1/directory/download).
- Update zip archive assembly services.
- Create events to remove user rights on an object - called after a file system object is deleted from Redis.
- Create basic directories (Music, Documents, etc.) for a new user.

### Added

## [0.0.6] 2023-06-16

### Added

- Support for user rights over interaction with file system objects (refinement of the first prototype):
    - deletion - when deleting a directory, check for all rights to objects located in this directory.
    - downloading - when downloading a directory, check for all rights to objects that are in this directory.
    - copying - when copying a directory, check for all rights to objects located in this directory.
    - moving - when moving a directory, check for all rights to objects located in this directory.
- Add functionality for renaming file system objects:
    - Add rules.
    - Basic functionality.
- A common interface for setting user data via JWT tokens.
- Module separation:
    - Redis
    - Services
- Update module documentation:
    - CreepTenuousImplants

### Fixed

- Fix a bug when deleting a directory - if there are objects in it, then delete them too in Redis.
- Fix a bug when copying a directory (error - directory not found).

## [0.0.5] 2023-06-11

### Added

- code refactoring:
    - Setting up beans
    - Setting up interfaces
    - Writing documentation

## [0.0.4] 2023-06-10

### Added

- Add rights to interact with file system objects (for users - based on Redis and JWT tokens) (First prototype):
    - viewing is the fundamental right on which all others will be based.
    - deletion.
    - Creating objects.
    - download.
    - upload to catalog.
    - downloading (dividing into files and directories).
    - copying.
    - moving - leave current rights to the owner.
    - All rights.
    - API methods for managing rights - create validation: the user cannot assign rights to himself.
- Improve atomic mode support:
    - Based on the class, transmit only the data that was created in this class (needed to encapsulate general data).
- API support:
    - Translate all API methods through a POST call - only those methods where the API communicates with services for
      the file system.
    - Add documentation to the API.
    - Convert all json format to project standard:
        - Unified error response format.

## [0.0.3] 2023-05-28

### Added

- Improve support for downloading files and directories.
- Improve support for uploading files and directories.
- Set prefixes for environment variables.
- Improve support for moving files and directories.
- Improve support for deleting files and directories:
    - Moving files to a temporary directory - necessary for the atomic mode of the file system.
- Enter atomic mode (will be part of the system kernel):
    - Creating classes to handle exceptions and implementing services in atomic mode. Different operations have
      different handlers (creation, deletion, copying, etc.).
    - Create an atomic mode context.
    - Creating context utilities for working with the file system (intermediary).

### Fixed

- if we copy the entire folder (method 1), then another folder is created and the copied folder is already in it.

## [0.0.2] 2023-05-08

### Added

- Ability to upload the same files.

## [0.0.1] 2023-04-22

### Added

- Viewing the file structure.
- Adding files.
- Deleting files.
- Adding directories.
- Removing directories.
- Moving Directories:
    - Moving Content.
    - Moving the directory itself.
- Moving files:
    - Single move.
    - Bulk move (from one directory level).
- Copying directories:
    - Copying content.
    - Copying the directory itself.
- Copying files:
    - Single copy.
    - Bulk copy (from one directory level).
- Uploading files.
- Downloading files.
- Downloading zip archives and unpacking them.
- Downloading directories (creating a zip archive).
