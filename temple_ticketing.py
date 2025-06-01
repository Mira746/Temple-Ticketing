from collections import deque

class Visitor:
    def __init__(self, name, age, category, token):
        self.name = name
        self.age = age
        self.category = category
        self.token = token

    def __str__(self):
        return f"{self.name} (Age: {self.age}, Category: {self.category}, Token: {self.token})"


class FairQueueTempleDarshan:
    def __init__(self):
        self.queues = {
            "VIP": deque(),
            "DISABLED": deque(),
            "SENIOR": deque(),
            "GENERAL": deque()
        }
        self.category_order = ["VIP", "DISABLED", "SENIOR", "GENERAL"]  # Priority order
        self.token_counter = 1  # Unique visitor ids for each person
        self.category_index = 0  # Keeps track of last category served
        self.total_served = 0  # Keeps track of number of people served

    def add_visitor(self, name, age, category):
        token = self.token_counter
        self.token_counter += 1

        # Auto-upgrade General category to Senior if age >= 60
        if category == "GENERAL" and age >= 60:
            print(f" Auto-upgraded {name} from GENERAL to SENIOR due to age.")
            category = "SENIOR"

        visitor = Visitor(name, age, category, token)
        self.queues[category].append(visitor)
        print(f"Added: {visitor}")

    def process_next_visitor(self):
        for _ in range(len(self.category_order)):
            current_category = self.category_order[self.category_index]
            if self.queues[current_category]:
                visitor = self.queues[current_category].popleft()
                print(f"\n Processing Visitor: {visitor}")
                print(f" Visitor with Token {visitor.token} is now proceeding for Darshan.")
                self.total_served += 1
                self.category_index = (self.category_index + 1) % len(self.category_order)
                return
            self.category_index = (self.category_index + 1) % len(self.category_order)
        print("\n No visitors in queue.")

    def peek_next_visitor(self):
        original_index = self.category_index
        for _ in range(len(self.category_order)):
            current_category = self.category_order[self.category_index]
            if self.queues[current_category]:
                print(f"\n Next in line: {self.queues[current_category][0]}")
                self.category_index = original_index
                return
            self.category_index = (self.category_index + 1) % len(self.category_order)
        print("\n No visitors in queue.")

    def show_queue(self):
        print("\n Current Queue Status:")
        for cat in self.category_order:
            print(f"{cat}:")
            for visitor in self.queues[cat]:
                print(f"  - {visitor}")
        print(f"\n Total Visitors Served: {self.total_served}\n")


# Run the system in a loop
temple_queue = FairQueueTempleDarshan()

while True:
    print("\n---- Temple Darshan Ticketing ----")
    print("1. Add Visitor")
    print("2. Process Next Visitor")
    print("3. Peek Next Visitor")
    print("4. Show Queue")
    print("5. Exit")
    choice = input("Enter your choice: ").strip()

    if choice == "1":
        name = input("Enter Visitor Name: ").strip()
        if not name:
            print(" Name cannot be empty.")
            continue
        try:
            age = int(input("Enter Age: ").strip())
        except ValueError:
            print(" Invalid age. Please enter a valid number.")
            continue
        category = input("Enter Category (VIP / Disabled / Senior / General): ").strip().upper()

        if category not in ["VIP", "DISABLED", "SENIOR", "GENERAL"]:
            print(" Invalid category. Please choose from VIP, Disabled, Senior, or General.")
            continue

        temple_queue.add_visitor(name, age, category)

    elif choice == "2":
        temple_queue.process_next_visitor()

    elif choice == "3":
        temple_queue.peek_next_visitor()

    elif choice == "4":
        temple_queue.show_queue()

    elif choice == "5":
        print("\n Exiting. Have a blessed day!")
        break

    else:
        print(" Invalid choice. Please enter a number between 1 and 5.")
