@ok
Feature: Clone documents in the same index
  Background:
    Given the plugin is installed in an elasticsearch cluster


  Scenario: A simple document can be cloned

    Given There are the following data in elasticsearch in the index "books"
      | id | title                                | author      |
      |aaa | The Devil in the White City          | Erik Larson |
      |bbb | The Lion, the Witch and the Wardrobe | C.S. Lewis  |

    When a POST request is performed to the endpoint "books/_doc_clone" with body
    """json
    {
      "src_id": "aaa",
      "dst_id": "ccc"
    }
    """

    Then The documents on index "books" are
      | id  | title                                | author      |
      | aaa | The Devil in the White City          | Erik Larson |
      | bbb | The Lion, the Witch and the Wardrobe | C.S. Lewis  |
      | ccc | The Devil in the White City          | Erik Larson |

