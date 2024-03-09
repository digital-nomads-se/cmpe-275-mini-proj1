import unittest
from common.message import Message

class TestMessage(unittest.TestCase):
    def test_encode_decode(self):
        original_message = "Test Message"
        encoded_message = Message.encode(original_message, "TestServer", "TestGroup")
        server_name, group_name, decoded_message = Message.decode(encoded_message)
        
        self.assertEqual(original_message, decoded_message)
        self.assertEqual("TestServer", server_name)
        self.assertEqual("TestGroup", group_name)

if __name__ == '__main__':
    unittest.main()